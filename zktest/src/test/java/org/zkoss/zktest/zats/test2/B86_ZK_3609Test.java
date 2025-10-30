/* B86_ZK_3609Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 07 11:46:40 CST 2019, Created by rudyhuang

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
public class B86_ZK_3609Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertFalse(jq("@navitem:contains(BB)").isVisible(),
				"BB shouldn't be rendered at first");

		click(jq("@button"));
		waitResponse();
		Assertions.assertFalse(jq("@navitem:contains(AA)").isVisible(),
				"AA shouldn't be visible");

		new Actions(driver)
				.moveToElement(toElement(jq("@nav:eq(1)")))
				.click(toElement(jq("@nav:eq(2)")))
				.perform();
		waitResponse(true);
		Assertions.assertTrue(jq("@navitem:contains(BCA)").isVisible(),
				"BCA should be visible");
	}
}
