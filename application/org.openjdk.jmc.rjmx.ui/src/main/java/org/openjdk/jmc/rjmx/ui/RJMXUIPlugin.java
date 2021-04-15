/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.openjdk.jmc.rjmx.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageRegistry;
import org.openjdk.jmc.rjmx.ui.internal.IconConstants;
import org.openjdk.jmc.rjmx.ui.internal.RJMXSingleton;
import org.openjdk.jmc.rjmx.ui.triggers.internal.NotificationRegistry;
import org.openjdk.jmc.ui.MCAbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;
p
/**
 * <p>
 * There is one instance of the RJMX UI plugin available from {@link #getDefault()}.
 * </p>
 * <p>
 * Clients may not instantiate or subclass this class.
 * </p>
 */
public final class RJMXUIPlugin extends MCAbstractUIPlugin {
	/** Plug-in ID for the ServicesUI plug-in. */
	public static final String PLUGIN_ID = "org.openjdk.jmc.rjmx.ui"; //$NON-NLS-1$

	/**
	 * The identifier for the server configuration.
	 */
	public static final String SERVER_CONFIG_ID = "serverConfig"; //$NON-NLS-1$

	// The shared instance.
	private static RJMXUIPlugin plugin;

	private IEclipsePreferences rjmxPreferences;

	/**
	 * The constructor.
	 */
	public RJMXUIPlugin() {
		super(PLUGIN_ID);
		plugin = this;
	}

	/**
	 * Return the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static RJMXUIPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// Avoid getPreferenceStore() so as not to create a store just for
		// saving it.
		synchronized (this) {
			if (rjmxPreferences != null) {
				RJMXSingleton.getDefault().storeAllSettings();
				rjmxPreferences.flush();
			}
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the plugin preferences for this plugin.
	 * <p>
	 * This {@link IEclipsePreferences} is used to hold persistent settings for this plugin in the
	 * context of a workbench. Some of these settings will be user controlled, whereas others may be
	 * internal setting that are never exposed to the user.
	 * <p>
	 * If an error occurs reading these settings, an empty settings container is quietly created,
	 * initialized with defaults, and returned.
	 *
	 * @return the preference store
	 */
	public synchronized IEclipsePreferences getRJMXPreferences() {
		// Create the preference store lazily.
		if (rjmxPreferences == null) {
			rjmxPreferences = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
		}
		return rjmxPreferences;
	}

	/**
	 * Looks up the server preferences for given server.
	 *
	 * @param serverUid
	 *            the identifier of the server
	 * @return the preferences available for given server.
	 */
	public synchronized Preferences getServerPreferences(String serverUid) {
		return getRJMXPreferences().node(SERVER_CONFIG_ID).node(serverUid);
	}

	public NotificationRegistry getNotificationRegistry() {
		return RJMXSingleton.getDefault().getNotificationRegistry();
	}

	/**
	 * Returns a global RJMX service. Currently there is no way to register new global services.
	 *
	 * @param <T>
	 *            the service type to look up
	 * @param clazz
	 *            the {@link Class} of the service
	 * @return the service object registered for the given class.
	 */
	public <T> T getService(Class<T> clazz) {
		return RJMXSingleton.getDefault().getService(clazz);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IconConstants.IMG_ATTRIBUTE_SELECTOR_BANNER,
				IconConstants.IMG_ATTRIBUTE_SELECTOR_BANNER);
		registerImage(registry, IconConstants.ICON_ATTRIBUTE_NORMAL, IconConstants.ICON_ATTRIBUTE_NORMAL);
		registerImage(registry, IconConstants.ICON_ATTRIBUTE_SYNTHETIC, IconConstants.ICON_ATTRIBUTE_SYNTHETIC);
		registerImage(registry, IconConstants.ICON_ATTRIBUTE_NUMERICAL, IconConstants.ICON_ATTRIBUTE_NUMERICAL);
		registerImage(registry, IconConstants.ICON_ATTRIBUTE_SYNTHETIC_NUMERICAL,
				IconConstants.ICON_ATTRIBUTE_SYNTHETIC_NUMERICAL);
		registerImage(registry, IconConstants.ICON_ATTRIBUTE_COMPOSITE, IconConstants.ICON_ATTRIBUTE_COMPOSITE);
		registerImage(registry, IconConstants.ICON_MBEAN, IconConstants.ICON_MBEAN);
		registerImage(registry, IconConstants.ICON_ADD_GRAPH_ATTRIBUTE, IconConstants.ICON_ADD_GRAPH_ATTRIBUTE);
		registerImage(registry, IconConstants.ICON_REMOVE_GRAPH_ATTRIBUTE, IconConstants.ICON_REMOVE_GRAPH_ATTRIBUTE);
		registerImage(registry, IconConstants.ICON_ADD_OBJECT, IconConstants.ICON_ADD_OBJECT);
		registerImage(registry, IconConstants.ICON_ADD_OBJECT_DISABLED, IconConstants.ICON_ADD_OBJECT_DISABLED);
		registerImage(registry, IconConstants.ICON_REMOVE_OBJECT, IconConstants.ICON_REMOVE_OBJECT);
		registerImage(registry, IconConstants.ICON_REMOVE_OBJECT_DISABLED, IconConstants.ICON_REMOVE_OBJECT_DISABLED);
		registerImage(registry, IconConstants.ICON_PERSISTENCE_TOGGLE_ON, IconConstants.ICON_PERSISTENCE_TOGGLE_ON);
		registerImage(registry, IconConstants.ICON_PERSISTENCE_TOGGLE_OFF, IconConstants.ICON_PERSISTENCE_TOGGLE_OFF);
		registerImage(registry, IconConstants.ICON_ACCESSIBILITY_MODE_TOGGLE_ON,
				IconConstants.ICON_ACCESSIBILITY_MODE_TOGGLE_ON);
		registerImage(registry, IconConstants.ICON_ACCESSIBILITY_MODE_TOGGLE_OFF,
				IconConstants.ICON_ACCESSIBILITY_MODE_TOGGLE_OFF);
		registerImage(registry, IconConstants.ICON_INSPECT, IconConstants.ICON_INSPECT);
		registerImage(registry, IconConstants.ICON_ERROR, IconConstants.ICON_ERROR);
		registerImage(registry, IconConstants.ICON_OPERATION_IMPACT_HIGH, IconConstants.ICON_OPERATION_IMPACT_HIGH);
		registerImage(registry, IconConstants.ICON_OPERATION_IMPACT_LOW, IconConstants.ICON_OPERATION_IMPACT_LOW);
		registerImage(registry, IconConstants.IMG_TOOLBAR_OVERVIEW, IconConstants.IMG_TOOLBAR_OVERVIEW);
	}

}
