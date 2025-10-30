/* B86_ZK_4342Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 16 12:27:23 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;

/**
 * @author rudyhuang
 */
public class B86_ZK_4342Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final Element inp = widget("@timebox").$n("real");
		click(inp);
		setCursorPosition(inp, 0);
		getActions().sendKeys(Keys.DOWN).perform();
		click(jq("@button"));
		waitResponse();
		Assertions.assertNotEquals("null", getZKLog());
		closeZKLog();

		click(inp);
		selectAll();
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("", jq(inp).val());
		Assertions.assertEquals("null", getZKLog());
	}
}
