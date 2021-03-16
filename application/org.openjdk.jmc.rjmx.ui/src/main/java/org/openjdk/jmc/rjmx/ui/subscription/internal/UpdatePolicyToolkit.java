package org.openjdk.jmc.rjmx.ui.subscription.internal;

import org.openjdk.jmc.rjmx.preferences.PreferencesKeys;
import org.openjdk.jmc.rjmx.subscription.internal.UpdatePolicyToolkitBase;
import org.openjdk.jmc.rjmx.ui.RJMXUIPlugin;

public class UpdatePolicyToolkit extends UpdatePolicyToolkitBase {

	protected UpdatePolicyToolkit() throws InstantiationException {
		super();
	}

	public static int getDefaultUpdateInterval() {
		return RJMXUIPlugin.getDefault().getRJMXPreferences().getInt(PreferencesKeys.PROPERTY_UPDATE_INTERVAL,
				PreferencesKeys.DEFAULT_UPDATE_INTERVAL);
	}
}
