/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 IBM Corporation. All rights reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The contents of this file are subject to the terms of either the Universal Permissive License
 * v 1.0 as shown at https://oss.oracle.com/licenses/upl
 *
 * or the following license:
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjdk.jmc.ui.websocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ClosedChannelException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jetty.websocket.api.Session;
import org.openjdk.jmc.ui.MCPathEditorInput;
import org.openjdk.jmc.ui.WorkbenchToolkit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Receives a JSON message with the name and URL of a recording, then downloads and opens it. The
 * expected format of the incoming message is: { downloadUrl: <url>, recordingName: <name>}
 */
public class DownloadRecordingHandler implements Session.Listener.AutoDemanding {
	private static final int CONNECT_TIMEOUT_MS = 30000;
	private static final int READ_TIMEOUT_MS = 60000;
	private static final int BUFFER_SIZE = 8192;

	// Trust manager that accepts all certificates (for self-signed certs)
	private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[] {new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	}};

	private Session session;
	private final ExecutorService downloadExecutor;

	public DownloadRecordingHandler(ExecutorService downloadExecutor) {
		this.downloadExecutor = downloadExecutor;
	}

	@Override
	public void onWebSocketOpen(Session session) {
		this.session = session;
		WebsocketPlugin.getLogger().log(Level.INFO, "Socket connected to " + session.getRemoteSocketAddress());
	}

	@Override
	public void onWebSocketText(String message) {
		WebsocketPlugin.getLogger().log(Level.INFO, "Received download request: " + message);

		if (message == null || message.trim().isEmpty()) {
			WebsocketPlugin.getLogger().log(Level.WARNING, "Received empty message");
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(message.trim());

			String downloadUrl = json.has("downloadUrl") ? json.get("downloadUrl").asText() : null;
			String recordingName = json.has("recordingName") ? json.get("recordingName").asText() : null;

			if (downloadUrl == null || downloadUrl.isEmpty()) {
				WebsocketPlugin.getLogger().log(Level.WARNING, "Could not extract download URL from message");
				return;
			}

			downloadExecutor.submit(() -> {
				try {
					downloadAndOpenRecording(downloadUrl, recordingName);
				} catch (Exception e) {
					WebsocketPlugin.getLogger().log(Level.SEVERE, "Failed to download recording from " + downloadUrl,
							e);
				}
			});
		} catch (Exception e) {
			WebsocketPlugin.getLogger().log(Level.SEVERE, "Failed to parse JSON message: " + message, e);
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		WebsocketPlugin.getLogger().log(Level.INFO, "Socket closed: [" + statusCode + "] " + reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		if (cause instanceof TimeoutException) {
			WebsocketPlugin.getLogger().log(Level.INFO, "Websocket timed out");
		} else if (cause instanceof ClosedChannelException) {
			WebsocketPlugin.getLogger().log(Level.INFO, "Websocket channel has closed");
		} else {
			WebsocketPlugin.getLogger().log(Level.SEVERE, "Websocket error", cause);
		}
	}

	private void downloadAndOpenRecording(String downloadUrl, String recordingName) throws Exception {
		URI uri = new URI(downloadUrl);
		URL url = uri.toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// For HTTPS, configure SSL to accept self-signed certificates
		if (connection instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
			try {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
				httpsConnection.setSSLSocketFactory(sc.getSocketFactory());
				httpsConnection.setHostnameVerifier((hostname, session) -> true);
			} catch (Exception e) {
				WebsocketPlugin.getLogger().log(Level.WARNING, "Failed to configure SSL", e);
			}
		}

		connection.setRequestMethod("GET");
		connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
		connection.setReadTimeout(READ_TIMEOUT_MS);

		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			String errorMsg = "Failed to download recording. HTTP response code: " + responseCode;
			throw new IOException(errorMsg);
		}

		String fileName = (recordingName != null && !recordingName.isEmpty()) ? recordingName : "recording.jfr";

		if (!fileName.endsWith(".jfr") && !fileName.endsWith(".jfr.gz")) {
			fileName = fileName + ".jfr";
		}

		File tempFile = File.createTempFile(fileName.replace(".jfr", "-"), ".jfr");
		tempFile.deleteOnExit();

		try (InputStream in = connection.getInputStream(); FileOutputStream out = new FileOutputStream(tempFile)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead;
			long totalBytes = 0;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				totalBytes += bytesRead;
			}
		} finally {
			connection.disconnect();
		}

		MCPathEditorInput editorInput = new MCPathEditorInput(tempFile, false);
		WorkbenchToolkit.asyncOpenEditor(editorInput);

		WebsocketPlugin.getLogger().log(Level.INFO, "Recording opened in JMC: " + fileName);
	}
}
