/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023, Red Hat Inc. All rights reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The contents of this file are subject to the terms of either the Universal Permissive License
 * v 1.0 as shown at http://oss.oracle.com/licenses/upl
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
package org.openjdk.jmc.browser.views;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.openjdk.jmc.ui.MCPathEditorInput;
import org.openjdk.jmc.ui.WorkbenchToolkit;
import org.openjdk.jmc.ui.misc.DisplayToolkit;

public class CryostatEndpoint extends WebSocketAdapter {

	private static final Logger logger = Logger.getLogger("CRYOSTAT");
	private final CountDownLatch closureLatch = new CountDownLatch(1);

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		logger.log(Level.WARNING, "Endpoint connected: " + sess);
	}

	private static class FileOpener implements Runnable {
		File file;

		@Override
		public void run() {
			WorkbenchToolkit.openEditor(new MCPathEditorInput(file, false));
		}
	}

	private File file;

	private void setFile(File file) {
		this.file = file;
	}

	private File getFile() {
		return file;
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		logger.log(Level.WARNING, "Receieved text message: " + message);

		// try opening the link: http://localhost:8181/api/beta/recordings/service:jmx:rmi:%2F%2F%2Fjndi%2Frmi:%2F%2Flocalhost:0%2Fjmxrmi/cryostat_test_recording_20230602T153326Z.jfr
		try {
			BufferedInputStream in = new BufferedInputStream(new URL(message).openStream());
			String filename = message.substring(message.lastIndexOf('/') + 1);
			if (filename.endsWith(".jfr")) {
				filename = filename.substring(0, filename.lastIndexOf('.'));
			}

			final File tempFile = File.createTempFile(filename, ".jfr");
			tempFile.deleteOnExit();
			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				IOUtils.copy(in, out);
			}
			setFile(tempFile);

			DisplayToolkit.safeSyncExec(new FileOpener() {
				@Override
				public void run() {
					WorkbenchToolkit.openEditor(new MCPathEditorInput(getFile(), false));
				}
			});
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		logger.log(Level.WARNING, "Socket Closed: [" + statusCode + "] :" + reason);
		closureLatch.countDown();
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}

	public void awaitClosure() throws InterruptedException {
		logger.log(Level.WARNING, "Awaiting closure from remote.");
		closureLatch.await();
	}
}
