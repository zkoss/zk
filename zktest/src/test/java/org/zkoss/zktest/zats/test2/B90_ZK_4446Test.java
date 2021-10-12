/* B90_ZK_4446Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 20 12:27:36 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.ClientWidget;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertFalse("re-creating the same item should not be possible", jq(".z-chosenbox-empty-creatable").isVisible());
		sendKeys(chosenboxInput, Keys.ENTER);
		waitResponse();
		Assert.assertEquals("there should be only one item",1, jq(".z-chosenbox-item").length());
	}
	
	private void typeWithoutBlur(ClientWidget locator, String text) {
		focus(locator);
		WebElement webElement = toElement(locator);
		webElement.clear();
		webElement.sendKeys(text);
	}
}
