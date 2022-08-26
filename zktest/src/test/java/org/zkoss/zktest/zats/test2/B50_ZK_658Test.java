/* B50_ZK_658Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 15:49:14 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_658Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq("@doublebox"));
		waitResponse();
		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		System.out.println(jq("@doublebox").val());
		Assertions.assertEquals("6.3", jq("@doublebox").val());
		
		click(jq("@button"));
		waitResponse();
		click(jq("@doublebox"));
		waitResponse();
		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		System.out.println(jq("@doublebox").val());
		Assertions.assertEquals("5.2", jq("@doublebox").val());
	}
}
