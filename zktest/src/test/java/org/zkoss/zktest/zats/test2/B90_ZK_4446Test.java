/* B90_ZK_4446Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 20 12:27:36 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.ClientWidget;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B90_ZK_4446Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery chosenboxInput = jq("@chosenbox input");
		typeWithoutBlur(chosenboxInput, "AAA");
		waitResponse();
		sendKeys(chosenboxInput, Keys.ENTER);
		waitResponse();
		sendKeys(chosenboxInput, Keys.BACK_SPACE);
		waitResponse();
		sendKeys(chosenboxInput, Keys.BACK_SPACE);
		waitResponse();
		typeWithoutBlur(chosenboxInput, "AAA");
		waitResponse(true);
		Assertions.assertFalse(jq(".z-chosenbox-empty-creatable").isVisible(),
				"re-creating the same item should not be possible");
		sendKeys(chosenboxInput, Keys.ENTER);
		waitResponse();
		Assertions.assertEquals(1, jq(".z-chosenbox-item").length(), "there should be only one item");
	}
	
	private void typeWithoutBlur(ClientWidget locator, String text) {
		focus(locator);
		WebElement webElement = toElement(locator);
		webElement.clear();
		webElement.sendKeys(text);
	}
}
