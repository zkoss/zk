/* B80_ZK_2927Test.java

	Purpose:
		
	Description:
		
	History:
		11:38 AM 10/21/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2927_2Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq("@button"));
			waitResponse();
			for (int i = 0; i < 3; i++) {
				click(jq("@button"));
				waitResponse();
			}
		} catch (Exception e) {
			fail();
		}
		assertNoAnyError();
	}
}