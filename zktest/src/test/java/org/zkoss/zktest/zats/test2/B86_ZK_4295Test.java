/* B86_ZK_4295Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 21 16:28:33 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4295Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Select select = new Select(driver.findElement(jq("@select").toBy()));
		select.selectByVisibleText("Item 2");
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("Item 2", jq(".target").text());
		Assertions.assertEquals("2", getZKLog());
	}
}
