/* B80_ZK_3110Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, May  4, 2016 12:32:17 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author sefi
 */
public class B80_ZK_3110Test extends ZATSTestCase {
	@Test public void test() {
		try {
			connect();
		} catch (Exception e){
			fail("Should not throw any exception");
		}
		assertTrue(true);
	}


}
