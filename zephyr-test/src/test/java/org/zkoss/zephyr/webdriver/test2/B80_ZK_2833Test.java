/* B80_ZK_2833Test.java

	Purpose:
		
	Description:
		
	History:
		9:35 AM 8/3/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2833Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			sleep(2000);
		} catch (Exception e) {
			fail("No exception here");
		}
		assertNoAnyError();
	}
}
