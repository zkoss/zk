/* F70_ZK_2085Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 15:11:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_2085Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		double tick = (12 - 10) / 0.05;
		int tickInPx = (int) Math.ceil(jq("@slider").width() / tick);
		WebElement btn = toElement(widget("@slider").$n("btn"));
		getActions().clickAndHold(btn)
				.moveByOffset(tickInPx * 4, 0)
				.perform();
		waitResponse();
		Assertions.assertEquals(10.2, Double.parseDouble(jq("@label:last").text()), 0.01);
	}
}
