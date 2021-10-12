/* B80_ZK_2921Test.java

	Purpose:
		
	Description:
		
	History:
		4:20 PM 10/16/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2921Test extends ZATSTestCase {

	@Test public void test() {
		try {
			connect();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}