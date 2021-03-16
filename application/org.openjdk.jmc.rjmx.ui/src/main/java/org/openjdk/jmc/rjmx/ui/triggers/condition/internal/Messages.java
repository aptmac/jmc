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
package org.openjdk.jmc.rjmx.ui.triggers.condition.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.openjdk.jmc.rjmx.triggers.condition.internal.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static final String TriggerCondition_ASCENDING_FLANK_CAPTION = "TriggerCondition_ASCENDING_FLANK_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_ASCENDING_FLANK_TOOLTIP = "TriggerCondition_ASCENDING_FLANK_TOOLTIP"; //$NON-NLS-1$
	public static final String TriggerCondition_DESCENDING_FLANK_CAPTION = "TriggerCondition_DESCENDING_FLANK_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_DESCENDING_FLANK_TOOLTIP = "TriggerCondition_DESCENDING_FLANK_TOOLTIP"; //$NON-NLS-1$
	public static final String TriggerCondition_LIMIT_PERIOD_CAPTION = "TriggerCondition_LIMIT_PERIOD_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_LIMIT_PERIOD_TOOLTIP = "TriggerCondition_LIMIT_PERIOD_TOOLTIP"; //$NON-NLS-1$
	public static final String TriggerCondition_MATCH_STRING_CAPTION = "TriggerCondition_MATCH_STRING_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_MATCH_STRING_TOOLTIP = "TriggerCondition_MATCH_STRING_TOOLTIP"; //$NON-NLS-1$
	public static final String TriggerCondition_MAX_TRIGGER_CAPTION = "TriggerCondition_MAX_TRIGGER_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_MAX_TRIGGER_TOOLTIP = "TriggerCondition_MAX_TRIGGER_TOOLTIP"; //$NON-NLS-1$
	public static final String TriggerCondition_MIN_TRIGGER_CAPTION = "TriggerCondition_MIN_TRIGGER_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_MIN_TRIGGER_TOOLTIP = "TriggerCondition_MIN_TRIGGER_TOOLTIP"; //$NON-NLS-1$
	public static final String TriggerCondition_SUSTAINED_CAPTION = "TriggerCondition_SUSTAINED_CAPTION"; //$NON-NLS-1$
	public static final String TriggerCondition_SUSTAINED_TOOLTIP = "TriggerCondition_SUSTAINED_TOOLTIP"; //$NON-NLS-1$

	private Messages() {
	}

    public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
