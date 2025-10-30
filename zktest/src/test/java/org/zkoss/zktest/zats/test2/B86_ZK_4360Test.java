/* B86_ZK_4360Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Aug 23 15:50:18 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4360Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		click(jq(".z-tab").eq(1));
		waitResponse(true);
		click(jq("iframe"));
		waitResponse();
		act.sendKeys("a").perform();
		waitResponse();
		Assertions.assertEquals("onChanging", getZKLog());
	}
}
