/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020, 2021 Red Hat Inc. All rights reserved.
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
package org.openjdk.jmc.rjmx.internal;

import org.openjdk.jmc.rjmx.servermodel.IServerModel;
import org.openjdk.jmc.rjmx.subscription.IMRIMetadataProviderService;
import org.openjdk.jmc.rjmx.subscription.IMRIMetadataService;
import org.openjdk.jmc.rjmx.subscription.internal.ExtensionMetadataProviderService;
import org.openjdk.jmc.rjmx.subscription.internal.FileMRIMetadataDB;
import org.openjdk.jmc.rjmx.servermodel.internal.ServerModel;

public abstract class RJMXSingletonBase {
	private final FileMRIMetadataDB metadataManager = buildMetadataManager();
	private final ServerModel serverModel = new ServerModel();

	/**
	 * The default constructor.
	 */
	protected RJMXSingletonBase() {
	}

	private static FileMRIMetadataDB buildMetadataManager() {
		IMRIMetadataProviderService subService = new ExtensionMetadataProviderService();
		return FileMRIMetadataDB.buildDefault(subService);
	}

	/**
	 * Returns a global RJMX service. Currently there is no way to register new global services.
	 *
	 * @param <T>
	 *            the service type to look up
	 * @param clazz
	 *            the {@link Class} of an class
	 * @return the service object registered for the given class.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> clazz) {
		if (clazz == IMRIMetadataService.class) {
			return (T) metadataManager;
		}
		if (clazz == IServerModel.class) {
			return (T) serverModel;
		}
		if (clazz == ServerModel.class) {
			return (T) serverModel;
		}
		return null;
	}
}
