/* ExceptionsTest.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommonTest/src/com/potix/lang/ExceptionsTest.java,v 1.2 2006/02/27 03:42:09 tomyeh Exp $
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

public class ExceptionsTest extends TestCase {
	public ExceptionsTest(String name) {
		super(name);
	}
	public static Test suite() {
		return new TestSuite(ExceptionsTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testException() throws Exception {
		String msg = "+++hello+++";
		Exception ex = new CommonException(msg);
		assertEquals(msg, ex.getMessage());

		try {
			assert false;
		}catch(Throwable t) {
			try {
				Exceptions.wrap(t, SystemException.class);
				assertTrue(false); //never here
			}catch(Throwable t2) {
				assertTrue(t2 instanceof AssertionError);
			}
		}
	}
}
