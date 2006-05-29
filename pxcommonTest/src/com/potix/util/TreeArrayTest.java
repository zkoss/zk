/* TreeArrayTest.java

{{IS_NOTE

	Purpose: Test TreeArray 
	Description: 
	History:
	 2001/5/10, Tom M. Yeh: Created.

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

public class TreeArrayTest extends TestCase {
	public TreeArrayTest(String name) {
		super(name);
	}
	public static Test suite() {
		return new TestSuite(TreeArrayTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testTreeArrayA() throws Exception {
		final int LOOP_NO = 10;

		TreeArrayDbg ta = new TreeArrayDbg();
		for (int j=0; j<LOOP_NO; ++j)
			ta.add(new Integer(j+LOOP_NO));
		assertTrue(LOOP_NO == ta.size());
		assertTrue(ta.check(System.out));

		for (int j=0; j<LOOP_NO; ++j)
			assertEquals(new Integer(j+LOOP_NO), ta.get(j));

		for (int j=0; j<LOOP_NO; ++j)
			ta.add(0, new Integer(LOOP_NO-1-j));
		assertTrue(LOOP_NO*2 == ta.size());
		assertTrue(ta.check(System.out));

		for (int j=0; j<LOOP_NO*2; ++j) {
			assertEquals(new Integer(j), ta.get(j));
			assertTrue(j == ta.indexOfEntry(ta.getEntry(j)));
		}

		for (int j=LOOP_NO; --j>=0;) {
			ListX.Entry e = ta.removeEntry(j);
			assertEquals(new Integer(j), e.getElement());
			assertTrue(e.isOrphan());
		}
		assertTrue(LOOP_NO == ta.size());
		assertTrue(ta.check(System.out));

		for (int j=0; j<LOOP_NO; ++j)
			assertEquals(new Integer(j+LOOP_NO), ta.get(j));

		ta.clear();
		assertTrue(0 == ta.size());
	}
	public void testTreeArrayB() throws Exception {
		final int EXT_LOOP_NO = 5;
		final int LOOP_NO = 200;
		Random rand = new Random();
		TreeArrayDbg ta = new TreeArrayDbg();
		Integer t = new Integer(101);
		for (int i=0, cnt=0; i<EXT_LOOP_NO; ++i) {
			//add LOOP_NO
			for (int j=0; j<LOOP_NO; ++j)
				ta.add(rand.nextInt(j+1), t);
			assertTrue(LOOP_NO+cnt == ta.size());
			assertTrue(ta.check(System.out));

			//remove LOOP_NO/2
			for (int j=LOOP_NO/2; --j>=0;)
				ta.remove(rand.nextInt(ta.size()));
			assertTrue(LOOP_NO/2+cnt == ta.size());
			assertTrue(ta.check(System.out));
			cnt += LOOP_NO/2;
		}
	}
}
