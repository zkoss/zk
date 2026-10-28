/* F50_2923266Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 16:52:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2923266Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@colorbox"));
		waitResponse();
		Assertions.assertTrue(jq(".z-colorbox-popup").isVisible());

		click(jq("@button"));
		waitResponse();

		// 2923266: dispatched, because Marble makes a disabled colorbox pointer-events:none and a pointer would never reach it
		eval("jq('@colorbox')[0].click()");
		waitResponse();
		Assertions.assertFalse(jq(".z-colorbox-popup").isVisible());
	}
}
