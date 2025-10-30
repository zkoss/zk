/* B50_2944355Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 16:35:37 CST 2019, Created by rudyhuang

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
public class B50_2944355Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery input = jq("@combobox input");
		click(input);
		sendKeys(input,"a");
		waitResponse();
		Assertions.assertEquals("bacus", getEval("window.getSelection().toString()"));

		sendKeys(input, Keys.DOWN); // select "abacus" first
		sendKeys(input, Keys.DOWN);
		sendKeys(input, Keys.DOWN);
		sendKeys(input, Keys.ENTER);
		Assertions.assertEquals("acuity", input.val());
	}
}
