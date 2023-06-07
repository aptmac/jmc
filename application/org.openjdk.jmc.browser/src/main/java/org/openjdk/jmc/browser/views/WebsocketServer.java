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

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeResponse;
import org.eclipse.jetty.websocket.server.JettyWebSocketCreator;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.websocket.servlet.WebSocketUpgradeFilter;

public class WebsocketServer {

	public class CryostatEndpointCreator implements JettyWebSocketCreator
	{
	    @Override
	    public Object createWebSocket(JettyServerUpgradeRequest jettyServerUpgradeRequest, JettyServerUpgradeResponse jettyServerUpgradeResponse)
	    {
	        return new CryostatEndpoint();
	    }
	}

	private static int MAX_MESSAGE_SIZE = 1024 * 1024 * 1024;
	private static int IDLE_TIMEOUT_MINUTES = 5;

	private final int port;
	private volatile boolean isConnected;
	private Server server;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public WebsocketServer(int port) {
		this.port = port;
		executorService.execute(() -> startServer());
	}

	public int getPort() {
		return port;
	}

	private void setIsConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	protected void startServer() {
		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setHost("127.0.0.1");
		connector.setPort(port);
		server.addConnector(connector);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		JettyWebSocketServletContainerInitializer.configure(context, (servletContext, container) -> {
			container.setMaxBinaryMessageSize(MAX_MESSAGE_SIZE);
			container.setIdleTimeout(Duration.ofMinutes(IDLE_TIMEOUT_MINUTES));
			container.addMapping("/cryostat/*", new CryostatEndpointCreator());
		});

		try {
			WebSocketUpgradeFilter.ensureFilter(context.getServletContext());
			Logger.getLogger("demo").log(Level.INFO,
					"Starting websocket server listening on port " + port);
			server.start();
			server.join();
			setIsConnected(true);
		} catch (Exception e) {
			Logger.getLogger("demo").log(Level.SEVERE, "Failed to start websocket server", e);
			setIsConnected(false);
		}
	}

	protected void shutdown() {
		try {
			Logger.getLogger("demo").log(Level.INFO,
					"Stopping websocket server listening on port " + port);
			server.stop();
			// TODO: see if we need to cleanup executor service and thread
			setIsConnected(false);
		} catch (Exception e) {
			Logger.getLogger("demo").log(Level.SEVERE, "Failed to stop websocket server", e);
			setIsConnected(true);
		}
	}
}
