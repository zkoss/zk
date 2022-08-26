/* B90_ZK_4339Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 09 17:40:58 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4339Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = getActions();
		actions.moveToElement(toElement(jq("@button:eq(0)")))
				.pause(500).click()
				.pause(600).perform();
		Assertions.assertFalse(jq("$tt").isVisible());

		actions.moveToElement(toElement(jq("@button:eq(1)")))
				.pause(500).click()
				.pause(600).perform();
		Assertions.assertFalse(jq("$tt").isVisible());

		actions.moveToElement(toElement(jq("@button:eq(2)")))
				.pause(500).contextClick()
				.pause(600).perform();
		Assertions.assertFalse(jq("$tt").isVisible());

		actions.moveToElement(toElement(jq("@button:eq(3)")))
				.pause(500).contextClick()
				.pause(600).perform();
		Assertions.assertFalse(jq("$tt").isVisible());
	}
}
