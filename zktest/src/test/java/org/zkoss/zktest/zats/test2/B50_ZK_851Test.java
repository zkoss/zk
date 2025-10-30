/* B50_ZK_851Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 23 15:37:38 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_851Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		click(jq("@button:first"));
		waitResponse();
		click(jq("@textbox"));
		waitResponse();
		act.sendKeys("123").perform();
		waitResponse();
		click(jq(".z-div"));
		waitResponse();
		click(jq("@button:last"));
		waitResponse();
		Assertions.assertEquals("abc123", jq(".z-messagebox-window .z-label").text().trim());
	}
}
