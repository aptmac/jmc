package org.openjdk.jmc.rjmx.ui.persistence.internal;

import java.io.File;

import org.openjdk.jmc.ui.common.CorePlugin;

public class PersistenceKeys {
	// Persistence
	public static final String PROPERTY_PERSISTENCE_LOG_ROTATION_LIMIT_KB = "rjmx.services.persistence.log.rotation.limit"; //$NON-NLS-1$
	public static final long DEFAULT_PERSISTENCE_LOG_ROTATION_LIMIT_KB = 100;
	public static final String PROPERTY_PERSISTENCE_DIRECTORY = "rjmx.services.persistence.directory"; //$NON-NLS-1$
	public static final String DEFAULT_PERSISTENCE_DIRECTORY = CorePlugin.getDefault().getWorkspaceDirectory().getPath()
			+ File.separator + "persisted_jmx_data" + File.separator; //$NON-NLS-1$
}
