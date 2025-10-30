/* B50_2936568Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 15:23:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2936568Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery input = jq("@timebox input");

		click(input);
		sendKeys(input, Keys.TAB);
		Assertions.assertEquals("", input.val());

		click(input);
		sendKeys(input, Keys.DOWN);
		sendKeys(input, Keys.TAB);
		Assertions.assertNotEquals("", input.val());
	}
}
