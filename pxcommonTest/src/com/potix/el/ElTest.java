/*	ElTest.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommonTest/src/com/potix/el/ElTest.java,v 1.2 2006/02/27 03:42:08 tomyeh Exp $
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
package com.potix.el;

import java.util.*;
import junit.framework.*;

/**
 * Test EL.
 *
 * @author <a href="mailto:henrichen@potix.com">Henri Chen</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:08 $
 */
public class ElTest extends TestCase {
	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ElTest.class);
	}

	/**
	 * Constructor.
	 */
	public ElTest(String name)	{
		super(name);
	}

	// Must have this.
	public static Test suite() {
		return new TestSuite(ElTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testEl() throws Exception {
		final Map vars = new HashMap();
		vars.put("a", "1");
		vars.put("b", new Integer(2));
		assertEquals("1+2", 
			new EvaluatorImpl().evaluate("${a}+${b}", String.class, new SimpleResolver(vars), null));
	}
}