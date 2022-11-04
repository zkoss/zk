/* B80_ZK_2982Test.java

	Purpose:
		
	Description:
		
	History:
		2:43 PM 11/23/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_2982Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			JQuery buttons = jq("button");
			click(buttons.eq(0));
			waitResponse();
			click(buttons.eq(1));
			waitResponse();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNoAnyError();
	}
}
