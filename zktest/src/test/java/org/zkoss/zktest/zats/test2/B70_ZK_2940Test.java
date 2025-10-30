/* B70_ZK_2940Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 16:06:47 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2940Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sendKeys(jq("body"), Keys.TAB, Keys.TAB, Keys.TAB);
		waitResponse();
		Assertions.assertTrue(jq("@combobutton:eq(0)").is(":focus"));
	}
}
