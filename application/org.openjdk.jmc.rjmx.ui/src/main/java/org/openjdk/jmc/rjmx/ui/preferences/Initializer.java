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
package org.openjdk.jmc.rjmx.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.openjdk.jmc.rjmx.preferences.PreferencesKeys;
import org.openjdk.jmc.rjmx.ui.RJMXUIPlugin;
import org.openjdk.jmc.rjmx.ui.internal.RJMXUIConstants;
import org.openjdk.jmc.rjmx.ui.persistence.internal.PersistenceKeys;

public class Initializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = RJMXUIPlugin.getDefault().getPreferenceStore();
		store.setDefault(RJMXUIConstants.PROPERTY_ASK_USER_BEFORE_MBEAN_UNREGISTER,
				RJMXUIConstants.DEFAULT_ASK_USER_BEFORE_MBEAN_UNREGISTER);
		store.setDefault(RJMXUIConstants.PROPERTY_MBEAN_PROPERTY_KEY_ORDER,
				RJMXUIConstants.DEFAULT_MBEAN_PROPERTY_KEY_ORDER);
		store.setDefault(RJMXUIConstants.PROPERTY_MBEAN_SUFFIX_PROPERTY_KEY_ORDER,
				RJMXUIConstants.DEFAULT_MBEAN_SUFFIX_PROPERTY_KEY_ORDER);
		store.setDefault(RJMXUIConstants.PROPERTY_MBEAN_PROPERTIES_IN_ALPHABETIC_ORDER,
				RJMXUIConstants.DEFAULT_MBEAN_PROPERTIES_IN_ALPHABETIC_ORDER);
		store.setDefault(RJMXUIConstants.PROPERTY_MBEAN_CASE_INSENSITIVE_PROPERTY_ORDER,
				RJMXUIConstants.DEFAULT_MBEAN_CASE_INSENSITIVE_PROPERTY_ORDER);
		store.setDefault(RJMXUIConstants.PROPERTY_MBEAN_SHOW_COMPRESSED_PATHS,
				RJMXUIConstants.DEFAULT_MBEAN_SHOW_COMPRESSED_PATHS);

		// Migrated from org.openjdk.jmc.rjmx.preferences for use with RJMXUIPlugin
		IEclipsePreferences preferences = DefaultScope.INSTANCE.getNode(RJMXUIPlugin.PLUGIN_ID);

		preferences.putInt(PreferencesKeys.PROPERTY_UPDATE_INTERVAL, PreferencesKeys.DEFAULT_UPDATE_INTERVAL);
		preferences.putInt(PreferencesKeys.PROPERTY_RETAINED_EVENT_VALUES,
				PreferencesKeys.DEFAULT_RETAINED_EVENT_VALUES);

		preferences.put(PreferencesKeys.PROPERTY_MAIL_SERVER, PreferencesKeys.DEFAULT_MAIL_SERVER);
		preferences.putInt(PreferencesKeys.PROPERTY_MAIL_SERVER_PORT, PreferencesKeys.DEFAULT_MAIL_SERVER_PORT);
		preferences.putBoolean(PreferencesKeys.PROPERTY_MAIL_SERVER_SECURE, PreferencesKeys.DEFAULT_MAIL_SERVER_SECURE);
		preferences.put(PreferencesKeys.PROPERTY_MAIL_SERVER_CREDENTIALS,
				PreferencesKeys.DEFAULT_MAIL_SERVER_CREDENTIALS);

		preferences.putLong(PersistenceKeys.PROPERTY_PERSISTENCE_LOG_ROTATION_LIMIT_KB,
				PersistenceKeys.DEFAULT_PERSISTENCE_LOG_ROTATION_LIMIT_KB);
		preferences.put(PersistenceKeys.PROPERTY_PERSISTENCE_DIRECTORY,
				new File(PersistenceKeys.DEFAULT_PERSISTENCE_DIRECTORY).getPath());
		preferences.putInt(PreferencesKeys.PROPERTY_LIST_AGGREGATE_SIZE, PreferencesKeys.DEFAULT_LIST_AGGREGATE_SIZE);
	}

}
