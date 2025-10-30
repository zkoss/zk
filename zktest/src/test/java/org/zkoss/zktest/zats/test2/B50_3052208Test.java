/* B50_3052208Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 26 17:17:15 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3052208Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = new Actions(driver);

		testMenu(actions, 0);
		testMenu(actions, 1);
		testMenu(actions, 0);
		testMenu(actions, 1);
		testMenu(actions, 0);
		testMenu(actions, 1);
		testMenu(actions, 0);
		testMenu(actions, 1);
	}

	private void testMenu(Actions actions, int index) {
		actions.moveToElement(toElement(jq("@menu:eq(" + index + ")"))).perform();
		Assertions.assertTrue(jq("@menupopup:eq(" + index + ")").isVisible());
	}
}
