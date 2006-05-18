/*	CollectionsXTest.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommonTest/src/com/potix/util/CollectionsXTest.java,v 1.2 2006/02/27 03:42:09 tomyeh Exp $
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

import junit.framework.*;

import java.util.*;

/**
 * Test ExtCollection.
 *
 * @author <a href="mailto:henrichen@potix.com">Henri Chen</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:09 $
 */
/* rename $TEST to your test class name */
public class CollectionsXTest extends TestCase {
	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(CollectionsXTest.class);
	}

	/**
	 * Constructor.
	 */
	public CollectionsXTest(String name)	{
		super(name);
	}

	// Must have this.
	public static Test suite() {
		return new TestSuite(CollectionsXTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	private void check(String s, String[] rts, char sep) throws Exception {
		Collection c = CollectionsX.parse(new LinkedList(), s, sep);
		assertEquals(new Integer(rts.length), new Integer(c.size()));
		final Iterator iter = c.iterator();
		for (int j=0; iter.hasNext(); ++j)
			assertEquals(rts[j], iter.next());
	}
	public void testParse0() throws Exception {
		String s = " a b , 'c d ','\\'ab''bm',df, 'x ' 'y' z m";
		String[] rts = {"a b", "c d ", "'ab", "bm", "df", "x ", "y", "z m"};
		check(s, rts, ',');
	}
	public void testParse1() throws Exception {
		String s = "a, , b, ";
		String[] rts = {"a", "", "b"};
		check(s, rts, ',');
	}
	public void testParse2() throws Exception {
		String s = "test=\"debit != null && debit != 0\"";
		String[] rts = {"test=", "debit != null && debit != 0"};
		check(s, rts, ' ');
	}
}
