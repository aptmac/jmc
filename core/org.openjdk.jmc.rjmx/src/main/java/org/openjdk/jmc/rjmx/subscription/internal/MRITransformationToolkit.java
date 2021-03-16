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
package org.openjdk.jmc.rjmx.subscription.internal;

import java.util.Properties;

import org.openjdk.jmc.rjmx.subscription.IMRITransformationFactory;

/**
 * An MRI transformation toolkit responsible for creating transformations from MRI, finding
 * attributes they depend on, etc. Will read available transformation factories from the extension
 * "org.openjdk.jmc.rjmx.attributeTransformation".
 */
public class MRITransformationToolkit extends MRITransformationToolkitBase {

	private MRITransformationToolkit() {
		super();
	}

	static {
		initializeFromExtensions();
	}

	private static void initializeFromExtensions() {		
		IMRITransformationFactory transformationFactory = new SingleMRITransformationFactory();
        String transformationName = "difference";
        Properties props = new Properties();
        props.put("visualizeLabel", "Visualize difference...");
        props.put("transformationClass", "org.openjdk.jmc.rjmx.subscription.internal.DifferenceTransformation");
        Properties transProps = new Properties();
        transProps.put("displayName", "%s (difference)");
        props.put(TRANSFORMATION_NAME_ATTRIBUTE, transformationName);
        transformationFactory.setFactoryProperties(props, transProps);
        TRANSFORMATION_FACTORIES.put(transformationName, transformationFactory);
        
        transformationFactory = new SingleMRITransformationFactory();
        transformationName = "rate";
        props = new Properties();
        props.put("visualizeLabel", "Visualize rate per second...");
        props.put("transformationClass", "org.openjdk.jmc.rjmx.subscription.internal.DifferenceTransformation");
        transProps = new Properties();
        transProps.put("displayName", "%s (rate per second)");
        transProps.put("rate", "1000");
        props.put(TRANSFORMATION_NAME_ATTRIBUTE, transformationName);
        transformationFactory.setFactoryProperties(props, transProps);
        TRANSFORMATION_FACTORIES.put(transformationName, transformationFactory);
        
        transformationFactory = new SingleMRITransformationFactory();
        transformationName = "average";
        props = new Properties();
        props.put("visualizeLabel", "Visualize average...");
        props.put("transformationClass", "org.openjdk.jmc.rjmx.subscription.internal.AverageTransformation");
        transProps = new Properties();
        transProps.put("terms", "30");
        transProps.put("displayName", "%%s (average over %s samples)");
        props.put(TRANSFORMATION_NAME_ATTRIBUTE, transformationName);
        transformationFactory.setFactoryProperties(props, transProps);
        TRANSFORMATION_FACTORIES.put(transformationName, transformationFactory);
        
        transformationFactory = new SingleMRITransformationFactory();
        transformationName = "delta";
        props = new Properties();
        props.put("visualizeLabel", "Visualize delta...");
        props.put("transformationClass", "org.openjdk.jmc.rjmx.subscription.internal.DeltaTransformation");
        transProps = new Properties();
        transProps.put("displayName", "%s (delta)");
        props.put(TRANSFORMATION_NAME_ATTRIBUTE, transformationName);
        transformationFactory.setFactoryProperties(props, transProps);
        TRANSFORMATION_FACTORIES.put(transformationName, transformationFactory);
	}
}
