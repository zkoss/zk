/*	ArraysXTest.java

{{IS_NOTE

	Purpose:
	Description:
	History:
		2001/05/18, Henri Chen: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util;

import java.lang.reflect.Array;

import junit.framework.*;

import com.potix.util.ArraysX;

/**
 * Test ArraysX.
 *
 * @author <a href="mailto:henrichen@potix.com">Henri Chen</a>
 */
public class ArraysXTest extends TestCase {
	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ArraysXTest.class);
	}

	/**
	 * Constructor.
	 */
	public ArraysXTest(String name)	{
		super(name);
	}

	// Must have this.
	public static Test suite() {
		return new TestSuite(ArraysXTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testConcat() throws Exception {
		int[] a = new int[]{1, 2, 3};
		int[] b = new int[]{4, 5};
		Object c = ArraysX.concat(a, b);
		
		assertTrue(c.getClass().isArray());
		assertEquals(Array.getLength(c), a.length+b.length);
		
		int[] c1 = (int[])c;
		/*dump
		for (int i = 0; i < c1.length; i++)
			System.out.println("+++" + i + ":" + c1[i]);
		*/	
		for (int i = 0; i < a.length; i++)
			assertEquals(a[i], c1[i]);
		for (int i = 0; i < b.length; i++)
			assertEquals(b[i], c1[i+a.length]);
	}
}