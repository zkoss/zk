/* B86_ZK_3609Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 07 11:46:40 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_3609Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertFalse(
				"BB shouldn't be rendered at first",
				jq("@navitem:contains(BB)").isVisible());

		click(jq("@button"));
		waitResponse();
		Assert.assertFalse(
				"AA shouldn't be visible",
				jq("@navitem:contains(AA)").isVisible());

		new Actions(driver)
				.moveToElement(toElement(jq("@nav:eq(1)")))
				.click(toElement(jq("@nav:eq(2)")))
				.perform();
		waitResponse(true);
		Assert.assertTrue(
				"BCA should be visible",
				jq("@navitem:contains(BCA)").isVisible());
	}
}
