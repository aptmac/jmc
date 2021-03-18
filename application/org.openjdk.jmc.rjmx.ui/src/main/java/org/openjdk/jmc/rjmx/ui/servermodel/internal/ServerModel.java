package org.openjdk.jmc.rjmx.ui.servermodel.internal;

import java.util.logging.Level;

import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.openjdk.jmc.common.IDescribable;
import org.openjdk.jmc.rjmx.ConnectionDescriptorBuilder;
import org.openjdk.jmc.rjmx.IConnectionDescriptor;
import org.openjdk.jmc.rjmx.IServerDescriptor;
import org.openjdk.jmc.rjmx.ui.RJMXUIPlugin;
import org.openjdk.jmc.rjmx.descriptorprovider.IDescriptorListener;
import org.openjdk.jmc.rjmx.descriptorprovider.IDescriptorProvider;
import org.openjdk.jmc.rjmx.servermodel.internal.DiscoveryInfo;
import org.openjdk.jmc.rjmx.servermodel.internal.Server;
import org.openjdk.jmc.rjmx.servermodel.internal.ServerModelBase;

public class ServerModel extends ServerModelBase {
	private static final String EXTENSIONPOINT_DESCRIPTORPROVIDER = "descriptorProvider"; //$NON-NLS-1$
	private final IDescriptorListener descriptorListener = new IDescriptorListener() {

		@Override
		public void onDescriptorRemoved(String descriptorId) {
			Server removedEntry = doRemove(descriptorId);
			if (removedEntry != null) {
				removedEntry.getServerHandle().dispose(false);
				modelChanged(null);
			}
		}

		@Override
		public void onDescriptorDetected(
			IServerDescriptor sd, String path, JMXServiceURL url, IConnectionDescriptor cd, IDescribable provider) {
			cd = cd != null ? cd : new ConnectionDescriptorBuilder().url(url).build();
			insert(new Server(path, url, null, new DiscoveryInfo(provider), sd, cd));
		}

	};

	public ServerModel() {
		super();
		setUpDiscoveryListeners();
	}

	private void setUpDiscoveryListeners() {
		IExtensionRegistry er = Platform.getExtensionRegistry();

		IConfigurationElement[] configs = er.getConfigurationElementsFor(RJMXUIPlugin.PLUGIN_ID,
				EXTENSIONPOINT_DESCRIPTORPROVIDER);
		for (IConfigurationElement config : configs) {
			try {
				if (config.getName().equals("provider")) { //$NON-NLS-1$
					// Only one provider per descriptorProvider.
					IDescriptorProvider provider = (IDescriptorProvider) config.createExecutableExtension("class"); //$NON-NLS-1$
					provider.addDescriptorListener(descriptorListener);
				}
			} catch (CoreException e) {
				RJMXUIPlugin.getDefault().getLogger().log(Level.WARNING, "Failed to start up a IDescriptorProvider!", e); //$NON-NLS-1$
			}
		}
	}
}
