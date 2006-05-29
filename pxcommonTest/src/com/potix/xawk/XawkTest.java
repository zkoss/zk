/* XawTest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  2 21:18:17  2002, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.xawk;

import java.util.List;
import junit.framework.*;

import com.potix.util.logging.Log;

public class XawkTest extends TestCase {
	public static final Log log = Log.lookup(XawkTest.class);

	private static final String XAWK1 = "/metainfo/com/potix/xawk/t1-xawk.xml";
	private static final String FILE1 = "/metainfo/com/potix/xawk/t1.xml";

	public XawkTest(String name) throws Exception {
		super(name);
		log.setLevel(Log.ALL);
	}
	public static Test suite() {
		return new TestSuite(XawkTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
		Log.lookup("com.potix.idom").setLevel(null);
	}
	public void testXawk1() throws Exception {
		Xawk xawk = new Xawk();
		xawk.config(XawkTest.class.getResourceAsStream(XAWK1));
		final List result = (List)
			xawk.parse(XawkTest.class.getResourceAsStream(FILE1), false);
		assertTrue(result != null);

		final String[] exp = new String[] {"A", "D", "E"};
		assertEquals(exp.length, result.size());
		for (int j = 0; j < exp.length; ++j)
			assertEquals(exp[j], result.get(j));
	}
}
