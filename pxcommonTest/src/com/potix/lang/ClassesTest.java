/* ClassesTest.java

{{IS_NOTE

	Purpose: Test MultiValues
	Description:
	History:
	 2001/4/12, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.lang;

import junit.framework.*;

import java.util.*;
import java.lang.reflect.Method;

public class ClassesTest extends TestCase {
	public ClassesTest(String name) {
		super(name);
	}
	public static Test suite() {
		return new TestSuite(ClassesTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testMethodName() throws Exception {
		assertEquals("a1", Classes.toAttributeName("setA1"));
		assertEquals("a1", Classes.toAttributeName("getA1"));
		assertEquals("a1", Classes.toAttributeName("isA1"));
		assertTrue(null == Classes.toAttributeName("isolate"));
		assertEquals("setA1", Classes.toMethodName("a1", "set"));
	}
	public void testTopmostInterface() throws Exception {
		assertEquals(TI111.class, Classes.getTopmostInterface(TC11.class, TI1.class));
	}
	public void testGetMethod() throws Exception {
		String[] exprs = {
			"indexOf(java.lang.String name, int from)",
			"int indexOf ( java.lang.String name ,int from )",
		};
		List list = new LinkedList();
		for (int j = 0; j < exprs.length; ++j) {
			Method m = Classes.getMethodBySignature(String.class,
				exprs[j], list);
			assertTrue(m.getParameterTypes().length == 2);
			assertTrue(list.size() == 2);
			assertEquals(String.class, m.getParameterTypes()[0]);
			assertEquals(Integer.TYPE, m.getParameterTypes()[1]);
			assertEquals("name", list.remove(0));
			assertEquals("from", list.remove(0));
		}
	}
	public void testGetCloseMethod() throws Exception {
		Classes.getCloseMethod(TI1.class, "f", new Class[] {Integer.class});
			//int and Integer can be used interchangeable for getCloseMethod
	}
	public static interface TI1 {
		public int f(int v);
		public int f(int v, int m);
	}
}
interface TI2 {
}
interface TI11 extends ClassesTest.TI1 {
}
interface TI111 extends TI2, TI11 {
}
abstract class TC1 implements TI111 {
}
abstract class TC11 extends TC1 implements TI2 {
}
