/* B80_ZK_2921Test.java

	Purpose:
		
	Description:
		
	History:
		4:20 PM 10/16/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2921Test extends WebDriverTestCase {

	@Test
	public void test() {
		try {
			connect();
			sleep(2000);
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNoAnyError();
	}
}