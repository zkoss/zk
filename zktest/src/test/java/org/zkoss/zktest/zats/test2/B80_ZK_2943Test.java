/* B80_ZK_2943Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  5 10:17:04 CST 2015, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * 
 * @author wenning
 */
public class B80_ZK_2943Test extends ZATSTestCase {

	@Test
	public void test() {
		try {
			connect();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
