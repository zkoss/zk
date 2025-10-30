/* B50_ZK_937Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 24 12:45:31 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_937Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		WebElement first = toElement(jq(".z-tab").eq(0));
		WebElement fourth = toElement(jq(".z-tab").eq(3));
		act.dragAndDrop(first, fourth).perform();
		waitResponse();
		Assertions.assertEquals("first", jq(".z-tab").eq(2).text());
		Assertions.assertEquals("block", jq(".z-tabpanels div:contains(3)").css("display"));
		Assertions.assertTrue(jq(".z-tab:contains(first)").hasClass("z-tab-selected"));
	}
}
