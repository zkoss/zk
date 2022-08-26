/* B86_ZK_3860Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 15:56:53 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3860Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		connect();
		waitResponse();
		focus(jq(".z-chosenbox-input"));
		WebElement input = toElement(jq(".z-chosenbox-input"));
		input.clear();
		input.sendKeys("ada");
		waitResponse();
		act.sendKeys(Keys.DOWN).perform();
		waitResponse();
		act.sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals("Adam adam@company.org", jq(".z-chosenbox-item").text());
	}
}
