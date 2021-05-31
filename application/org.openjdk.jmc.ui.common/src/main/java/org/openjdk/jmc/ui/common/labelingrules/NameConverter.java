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
package org.openjdk.jmc.ui.common.labelingrules;

import java.util.Collections;
import java.util.logging.Level;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.openjdk.jmc.common.jvm.JVMCommandLineToolkit;
import org.openjdk.jmc.common.jvm.JVMDescriptor;
import org.openjdk.jmc.common.labelingrules.NameConverterBase;
import org.openjdk.jmc.common.labelingrules.NamingRule;
import org.openjdk.jmc.common.resource.Resource;
import org.openjdk.jmc.common.util.Environment;
import org.openjdk.jmc.ui.common.CorePlugin;
import org.openjdk.jmc.ui.common.idesupport.IDESupportFactory;

/**
 * Converts names according to rules specified with the {@value #LABELING_RULES_EXTENSION_POINT}
 * extension point.
 */
public final class NameConverter extends NameConverterBase {
	private static final String LABELING_RULES_EXTENSION_POINT = "org.openjdk.jmc.ui.common.labelingRules"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	private static final String UNKNOWN_LOCAL_JVM = "Local"; //$NON-NLS-1$

	private static final NameConverter INSTANCE = new NameConverter();

	/**
	 * @return a singleton instance
	 */
	public static NameConverter getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a new name converter instance. This should only be used if you want a new, clean
	 * instance. Normally the {@link NameConverter#getInstance()} method should be used instead to
	 * get a singleton instance.
	 */
	public NameConverter() {
		initializeRulesFromExtensions();
	}

	public Resource getImageResource(JVMDescriptor descriptor) {
		NamingRule rule = getMatchingRule(prepareValues(descriptor));
		return rule == null ? null : rule.getImageResource();
	}

	private void initializeRulesFromExtensions() {
		IExtensionRegistry er = Platform.getExtensionRegistry();
		IExtensionPoint ep = er.getExtensionPoint(LABELING_RULES_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configs = extension.getConfigurationElements();
			for (IConfigurationElement config : configs) {
				if (config.getName().equals("rule")) { //$NON-NLS-1$
					try {
						rules.add(createRule(config));
					} catch (Exception e) {
						CorePlugin.getDefault().getLogger().log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}
		Collections.sort(rules, COMPARATOR);
	}

	private NamingRule createRule(IConfigurationElement config) throws Exception {
		String name = config.getAttribute("name"); //$NON-NLS-1$
		// Try/Catch here to at least have a chance of providing the user with a hint
		// should something go wrong.
		try {
			int priority = Integer.parseInt(config.getAttribute("priority")); //$NON-NLS-1$
			String matchingPart = config.getAttribute("match"); //$NON-NLS-1$
			String formattingPart = config.getAttribute("format"); //$NON-NLS-1$
			return new NamingRule(name, matchingPart, formattingPart, priority, getIcon(config));
		} catch (Exception e) {
			throw new Exception("Problem instantiating naming rule named " + name); //$NON-NLS-1$
		}
	}

	private Resource getIcon(IConfigurationElement configElement) {
		String iconName = configElement.getAttribute(ATTRIBUTE_ICON);
		if (iconName != null) {
			String extendingPluginId = configElement.getDeclaringExtension().getContributor().getName();
			return new Resource(extendingPluginId, iconName);
		}
		return null;
	}

	@Override
	protected String getValidName(JVMDescriptor descriptor) {
		Integer pid = descriptor.getPid();
		if (pid != null && pid.intValue() == Environment.getThisPID() && descriptor.isAttachable()) {
			return IDESupportFactory.getIDESupport().getIdentity() + ".this"; //$NON-NLS-1$
		}
		String name = JVMCommandLineToolkit.getMainClassOrJar(descriptor.getJavaCommand());
		if (name != null && name.length() > 0) {
			return name;
		}
		return UNKNOWN_LOCAL_JVM;
	}
}
