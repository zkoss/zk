/* CacheMapTest.java

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
package com.potix.util;

import junit.framework.*;
import java.util.*;
import java.io.*;

import com.potix.util.logging.Log;

public class CacheMapTest extends TestCase {
	public static final Log log = Log.lookup(CacheMapTest.class);

	public CacheMapTest(String name) {
		super(name);
		log.setLevel(Log.ALL);
	}
	public static Test suite() {
		return new TestSuite(CacheMapTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testCacheMap() throws Exception {
		//Log.lookup("com.potix.util").setLevel(Log.DEBUG);
		//log.info("max memory: " + Runtime.getRuntime().maxMemory());

		Map map = new CacheMap().setLifetime(2000);
		map.put("a", "x");
		map.put("b", "y");
		map.put("c", "z");

		assertEquals(3, map.size());

		Iterator it = map.keySet().iterator();
		assertEquals("a", it.next());
		assertEquals("b", it.next());
		assertEquals("c", it.next());

		assertEquals("y", map.get("b"));
		it = map.keySet().iterator();
		assertEquals("a", it.next());
		assertEquals("c", it.next());
		assertEquals("b", it.next());

		assertEquals("z", map.get("c"));
		assertEquals("x", map.get("a"));
		it = map.keySet().iterator();
		assertEquals("b", it.next());
		assertEquals("c", it.next());
		assertEquals("a", it.next());

		System.gc();
		assertEquals(3, map.size());

		Thread.sleep(2000); //let it expire
		assertEquals("x", map.get("a")); //make it a live
		System.gc();
		assertEquals(1, map.size());
		assertEquals("x", map.get("a"));
	}
}
