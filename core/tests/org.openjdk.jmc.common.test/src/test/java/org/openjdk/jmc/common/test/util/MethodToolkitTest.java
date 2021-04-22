package org.openjdk.jmc.common.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openjdk.jmc.common.util.MethodToolkit;

@SuppressWarnings("nls")
public class MethodToolkitTest {

	@Test
	public void testIsPrimitive() {
		assertTrue(MethodToolkit.isPrimitive("int"));
		assertFalse(MethodToolkit.isPrimitive("char[]"));
		assertFalse(MethodToolkit.isPrimitive("org.foo.MyClass"));
	}

	@Test
	public void testNestedTypes() {
		assertNull(MethodToolkit.nestedTypes("java.lang.String"));
		assertEquals("Apa", MethodToolkit.nestedTypes("org.foo.MyClass$Apa"));
		assertEquals("Apa$Banan", MethodToolkit.nestedTypes("org.foo.MyClass$Apa$Banan"));
	}
}
