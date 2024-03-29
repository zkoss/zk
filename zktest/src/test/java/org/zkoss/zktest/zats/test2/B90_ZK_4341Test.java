/* B90_ZK_4341Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 29 18:42:34 CST 2019, Created by rudyhuang

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
public class B90_ZK_4341Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final Element inp = widget("@datebox").$n("real");
		click(inp);
		type(inp, "2019 01 01");
		waitResponse();

		Assertions.assertEquals("2019 01 01", inp.get("value"));

		sendKeys(inp, Keys.ARROW_LEFT, Keys.SPACE);
		waitResponse();

		Assertions.assertNotEquals("2019 01 01", inp.get("value"));
	}
}
