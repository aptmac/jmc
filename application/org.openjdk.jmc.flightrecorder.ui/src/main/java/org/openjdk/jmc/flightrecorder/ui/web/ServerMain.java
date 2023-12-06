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
package org.openjdk.jmc.flightrecorder.ui.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHandler;

import org.openjdk.jmc.flightrecorder.ui.FlightRecorderUI;
import org.openjdk.jmc.flightrecorder.ui.overview.ResultReportUiServlet;

public class ServerMain {
    private final int port;
    private Server server;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ServerMain(int port) {
        this.port = port;
        executorService.execute(() -> startServer());
    }

    public int getPort() {
        return port;
    }

    private void startServer() {
        server = new Server(port);
//        WebAppContext webAppContext = new WebAppContext();
        // Look for annotations in classes and packages in the top level directory.
//        webAppContext.setAttribute(
//            "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/");
//        server.setHandler(webAppContext);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(ResultReportUiServlet.class, "/hello");
        server.setHandler(servletHandler);
        try {
			FlightRecorderUI.getDefault().getLogger().log(Level.INFO,
					"Starting web server listening on port " + port);
            server.start();
            server.join();
        } catch (Exception e) {
        	FlightRecorderUI.getDefault().getLogger().log(Level.SEVERE, "Failed to start web server", e);
        }
    }

    public void shutdown() {
		try {
			FlightRecorderUI.getDefault().getLogger().log(Level.INFO,
					"Stopping web server listening on port " + port);
			server.stop();
		} catch (Exception e) {
			FlightRecorderUI.getDefault().getLogger().log(Level.SEVERE, "Failed to stop web server", e);
		}
    }
}
