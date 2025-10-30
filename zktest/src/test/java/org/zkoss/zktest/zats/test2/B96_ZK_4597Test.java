/* B96_ZK_4597Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 10 10:35:32 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4597Test extends WebDriverTestCase {
	@Test
	public void testInit() {
		connect();
		waitResponse();
		Actions action = getActions();
		click(jq("@textbox:eq(0)"));
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertTrue(jq("@combobutton:eq(1)").is(":focus"));
	}
	@Test
	public void testDynamic() {
		connect();
		waitResponse();
		Actions action = getActions();
		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@textbox:eq(0)"));
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertTrue(jq("@textbox:eq(1)").is(":focus"));
	}
}
