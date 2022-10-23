/* B80_ZK_2895Test.java

	Purpose:
		
	Description:
		
	History:
		2:53 PM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2895Test extends ZephyrClientMVVMTestCase {
	@Test
	public void testZK2895() {
		try {
			connect();
			sleep(2000);
		} catch (Exception e) {
			fail();
		}
		assertNoAnyError();
	}
}
