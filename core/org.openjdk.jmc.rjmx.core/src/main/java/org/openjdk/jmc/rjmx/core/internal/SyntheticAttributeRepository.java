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
package org.openjdk.jmc.rjmx.core.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import org.openjdk.jmc.rjmx.core.IPropertySyntheticAttribute;
import org.openjdk.jmc.rjmx.core.ISyntheticAttribute;
import org.openjdk.jmc.rjmx.core.RJMXCorePlugin;
import org.openjdk.jmc.rjmx.core.subscription.MRI;
import org.openjdk.jmc.rjmx.core.subscription.internal.AttributeValueToolkit;
import org.openjdk.jmc.rjmx.core.subscription.internal.DeadlockedThreadCountAttribute;
import org.openjdk.jmc.rjmx.core.subscription.internal.DivisionAttribute;
import org.openjdk.jmc.rjmx.core.subscription.internal.HotSpotLastGcAttribute;
import org.openjdk.jmc.rjmx.core.subscription.internal.LongDifferenceAttribute;
import org.openjdk.jmc.rjmx.core.subscription.internal.MonitoredDeadlockedThreadCountAttribute;
import org.openjdk.jmc.rjmx.core.subscription.internal.HotSpotLiveSetAttribute;

/**
 * Contains all the synthetic attributes.
 */
public final class SyntheticAttributeRepository {

	private final Map<ObjectName, SyntheticAttributeMBeanEntry> mbeans = new HashMap<>();
	private final MBeanServer server = MBeanServerFactory.newMBeanServer();
	private final MBeanServerConnection compoundServer;

	public SyntheticAttributeRepository(MBeanServerConnection compoundServer) {
		this.compoundServer = compoundServer;
	}

	void initializeFromExtensions() {
		List<SyntheticAttributeEntry> attributeCandidates = new ArrayList<>();
		
        ISyntheticAttribute attribute = new LongDifferenceAttribute();
        MRI descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=Memory/FreeHeapMemory");
        Map<String, Object> properties = new HashMap<>();
        properties.put("minuend", "attribute://java.lang:type=Memory/HeapMemoryUsage/committed");
        properties.put("subtrahend", "attribute://java.lang:type=Memory/HeapMemoryUsage/used");
        ((IPropertySyntheticAttribute) attribute).setProperties(properties);
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "long", true, false, false));

        attribute = new LongDifferenceAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=Memory/FreeNonHeapMemory");
        properties = new HashMap<>();
        properties.put("minuend", "attribute://java.lang:type=Memory/NonHeapMemoryUsage/committed");
        properties.put("subtrahend", "attribute://java.lang:type=Memory/NonHeapMemoryUsage/used");
        ((IPropertySyntheticAttribute) attribute).setProperties(properties);
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "long", true, false, false));
               
        attribute = new DivisionAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=Memory/HeapMemoryUsagePercent");
        properties = new HashMap<>();
        properties.put("dividend", "attribute://java.lang:type=Memory/HeapMemoryUsage/used");
        properties.put("divisor", "attribute://java.lang:type=Memory/HeapMemoryUsage/committed");
        ((IPropertySyntheticAttribute) attribute).setProperties(properties);
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "double", true, false, false));
        
        attribute = new DivisionAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=OperatingSystem/PhysicalMemoryUsagePercent");
        properties = new HashMap<>();
        properties.put("dividend", "attribute://java.lang:type=OperatingSystem/UsedPhysicalMemorySize");
        properties.put("divisor", "attribute://java.lang:type=OperatingSystem/TotalPhysicalMemorySize");
        ((IPropertySyntheticAttribute) attribute).setProperties(properties);
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "double", true, false, false));
        
        attribute = new LongDifferenceAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=OperatingSystem/UsedPhysicalMemorySize");
        properties = new HashMap<>();
        properties.put("minuend", "attribute://java.lang:type=OperatingSystem/TotalPhysicalMemorySize");
        properties.put("subtrahend", "attribute://java.lang:type=OperatingSystem/FreePhysicalMemorySize");
        ((IPropertySyntheticAttribute) attribute).setProperties(properties);
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "long", true, false, false));
        
        attribute = new LongDifferenceAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=OperatingSystem/UsedSwapSpaceSize");
        properties = new HashMap<>();
        properties.put("minuend", "attribute://java.lang:type=OperatingSystem/TotalSwapSpaceSize");
        properties.put("subtrahend", "attribute://java.lang:type=OperatingSystem/FreeSwapSpaceSize");
        ((IPropertySyntheticAttribute) attribute).setProperties(properties);
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "long", true, false, false));
        
        attribute = new DeadlockedThreadCountAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=Threading/DeadlockedThreadCount");
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "int", true, false, false));
        
        attribute = new MonitoredDeadlockedThreadCountAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://java.lang:type=Threading/MonitoredDeadlockedThreadCount");
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, null, "int", true, false, false));
        
        attribute = new HotSpotLiveSetAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://com.sun.management:type=GarbageCollectionAggregator/HeapLiveSet");
        String description = "The remaining heap memory after the last major GC, measured in percent of committed heap.";
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, description, "double", true, false, false));
		
        attribute = new HotSpotLastGcAttribute();
        descriptor = MRI.createFromQualifiedName("attribute://com.sun.management:type=GarbageCollectionAggregator/LastGcInfo");
        description = "Information from the last time a garbage collection took place.";
        attributeCandidates.add(new SyntheticAttributeEntry(attribute, descriptor, description, "javax.management.openmbean.CompositeData", true, false, false));
        
		boolean hasResolved = true;
		while (!attributeCandidates.isEmpty() && hasResolved) {
			hasResolved = false;
			Iterator<SyntheticAttributeEntry> iterator = attributeCandidates.iterator();
			while (iterator.hasNext()) {
				SyntheticAttributeEntry candidate = iterator.next();
				if (candidate.getAttribute().hasResolvedDependencies(compoundServer)) {
					hasResolved = true;
					iterator.remove();
					candidate.getAttribute().init(compoundServer);
					registerEntry(candidate);
				}
			}
		}
//		IExtensionRegistry er = Platform.getExtensionRegistry();
//		IExtensionPoint ep = er.getExtensionPoint("org.openjdk.jmc.rjmx.syntheticattribute"); //$NON-NLS-1$
//		IExtension[] extensions = ep.getExtensions();
//		List<SyntheticAttributeEntry> attributeCandidates = new ArrayList<>();
//		for (IExtension extension : extensions) {
//			IConfigurationElement[] configs = extension.getConfigurationElements();
//			for (IConfigurationElement config : configs) {
//				if (config.getName().equals("syntheticAttribute")) { //$NON-NLS-1$
//					try {
//						ISyntheticAttribute attribute = (ISyntheticAttribute) config.createExecutableExtension("class"); //$NON-NLS-1$
//						SyntheticAttributeEntry candidate = createEntry(attribute, config);
//						attributeCandidates.add(candidate);
//					} catch (CoreException e) {
//						RJMXPlugin.getDefault().getLogger().log(Level.SEVERE,
//								"Could not instantiate synthetic attribute!", e); //$NON-NLS-1$
//					}
//				}
//			}
//		}
//		boolean hasResolved = true;
//		while (!attributeCandidates.isEmpty() && hasResolved) {
//			hasResolved = false;
//			Iterator<SyntheticAttributeEntry> iterator = attributeCandidates.iterator();
//			while (iterator.hasNext()) {
//				SyntheticAttributeEntry candidate = iterator.next();
//				if (candidate.getAttribute().hasResolvedDependencies(compoundServer)) {
//					hasResolved = true;
//					iterator.remove();
//					candidate.getAttribute().init(compoundServer);
//					registerEntry(candidate);
//				}
//			}
//		}
	}

	private void registerEntry(SyntheticAttributeEntry attributeEntry) {
		ObjectName objectName = attributeEntry.getAttributeDescriptor().getObjectName();
		SyntheticAttributeMBeanEntry entry = mbeans.get(objectName);
		if (entry == null) {
			entry = new SyntheticAttributeMBeanEntry(compoundServer);
			try {
				server.registerMBean(entry, objectName);
				mbeans.put(objectName, entry);
			} catch (Exception e) {
				RJMXCorePlugin.getDefault().getLogger().log(Level.SEVERE,
						"Could not register MBean for synthetic attribute!", e); //$NON-NLS-1$
			}
		}
		entry.addSyntheticAttribute(attributeEntry);
	}

//	private SyntheticAttributeEntry createEntry(ISyntheticAttribute attribute, IConfigurationElement config) {
//		String attributeName = config.getAttribute("attributeName"); //$NON-NLS-1$
//		String description = config.getAttribute("description"); //$NON-NLS-1$
//		String type = config.getAttribute("type"); //$NON-NLS-1$
//		boolean readable = Boolean.valueOf(config.getAttribute("readable")); //$NON-NLS-1$
//		boolean writeable = Boolean.valueOf(config.getAttribute("writeable")); //$NON-NLS-1$
//		boolean isIs = Boolean.valueOf(config.getAttribute("isIs")); //$NON-NLS-1$
//		if (attribute instanceof IPropertySyntheticAttribute) {
//			Map<String, Object> properties = parseProperties(config.getChildren("properties")); //$NON-NLS-1$
//			((IPropertySyntheticAttribute) attribute).setProperties(properties);
//		}
//		MRI descriptor = MRI.createFromQualifiedName(attributeName);
//		return new SyntheticAttributeEntry(attribute, descriptor, description, type, readable, writeable, isIs);
//	}
//
//	private Map<String, Object> parseProperties(IConfigurationElement[] children) {
//		if (children == null || children.length == 0) {
//			return Collections.emptyMap();
//		}
//		Map<String, Object> properties = new HashMap<>();
//		for (IConfigurationElement child : children[0].getChildren()) {
//			parseProperty(child, properties);
//		}
//		return properties;
//	}

//	private void parseProperty(IConfigurationElement child, Map<String, Object> properties) {
//		String key = child.getAttribute("key"); //$NON-NLS-1$
//		try {
//			if ("string".equals(child.getName())) { //$NON-NLS-1$
//				properties.put(key, child.getAttribute("value")); //$NON-NLS-1$
//			} else if ("boolean".equals(child.getName())) { //$NON-NLS-1$
//				properties.put(key, Boolean.valueOf(child.getAttribute("value"))); //$NON-NLS-1$
//			} else if ("integer".equals(child.getName())) { //$NON-NLS-1$
//				properties.put(key, Integer.parseInt(child.getAttribute("value"))); //$NON-NLS-1$
//			} else if ("float".equals(child.getName())) { //$NON-NLS-1$
//				properties.put(key, Float.parseFloat(child.getAttribute("value"))); //$NON-NLS-1$
//			}
//		} catch (NumberFormatException e) {
//			properties.put(key, e.getMessage());
//		}
//	}

	public String[] getDomains() {
		return server.getDomains();
	}

	public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException {
		return server.getObjectInstance(name);
	}

	public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) {
		return server.queryMBeans(name, query);
	}

	public Set<ObjectName> queryNames(ObjectName name, QueryExp query) {
		return server.queryNames(name, query);
	}

	public boolean isRegistered(ObjectName name) {
		return server.isRegistered(name);
	}

	public Integer getMBeanCount() {
		return server.getMBeanCount();
	}

	public boolean hasAttribute(ObjectName name, String dataPath) {
		SyntheticAttributeMBeanEntry entry = mbeans.get(name);
		if (entry == null) {
			return false;
		}
		return entry.hasDataPath(dataPath);
	}

	public MBeanInfo getMBeanInfo(ObjectName name)
			throws IntrospectionException, InstanceNotFoundException, ReflectionException {
		return server.getMBeanInfo(name);
	}

	public Object getAttribute(ObjectName name, String attribute) throws AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return AttributeValueToolkit.getAttribute(server, name, attribute);
	}

	public AttributeList getExistingAttributes(ObjectName name, String[] attributes)
			throws InstanceNotFoundException, ReflectionException, IOException {
		List<String> syntheticAttributes = new ArrayList<>();
		for (String attribute : attributes) {
			if (hasAttribute(name, attribute)) {
				syntheticAttributes.add(attribute);
			}
		}
		if (syntheticAttributes.size() > 0) {
			return AttributeValueToolkit.getAttributes(server, name, syntheticAttributes);
		}
		return new AttributeList();
	}

	public void setAttribute(ObjectName name, Attribute attribute)
			throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException,
			ReflectionException, MBeanException, IOException {
		server.setAttribute(name, attribute);
	}

	public AttributeList setExistingAttributes(ObjectName name, AttributeList attributes)
			throws InstanceNotFoundException, ReflectionException, IOException {
		AttributeList existingAttributes = new AttributeList();
		for (Object obj : attributes) {
			Attribute attribute = (Attribute) obj;
			if (hasAttribute(name, attribute.getName())) {
				existingAttributes.add(attribute);
			}
		}
		if (existingAttributes.size() > 0) {
			return server.setAttributes(name, existingAttributes);
		}
		return new AttributeList();
	}

	public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException {
		return server.isInstanceOf(name, className);
	}

	public void dispose() {
		for (SyntheticAttributeMBeanEntry mbeanEntry : mbeans.values()) {
			for (SyntheticAttributeEntry entry : mbeanEntry.getSyntheticAttributes()) {
				try {
					entry.getAttribute().stop();
				} catch (Throwable t) {
					// silently ignore
				}
			}
		}
	}
}
