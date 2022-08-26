/* B70_ZK_2935Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 15:40:11 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2935Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@row"));
		waitResponse();
		sendKeys(jq("body"), Keys.TAB);
		waitResponse();

		Assertions.assertEquals("false", getEval("document.activeElement === jq(\"@button\").get(0)"));
	}
}
