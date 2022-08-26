/* B50_2984235Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 12 17:52:02 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2984235Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertFalse(jq("@popup").isVisible());
		getActions().moveToElement(toElement(jq("@textbox")))
				.pause(1000)
				.perform();
		Assertions.assertTrue(jq("@popup").isVisible());

		getActions().moveToElement(toElement(jq("@combobox")))
				.pause(1000)
				.perform();
		Assertions.assertTrue(jq("@popup").isVisible());
	}
}
