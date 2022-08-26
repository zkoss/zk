/* B50_2955382Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 15:10:48 CST 2019, Created by rudyhuang

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
public class B50_2955382Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@combobox").$n("btn"));
		waitResponse();

		new Actions(driver)
			.moveToElement(driver.findElement(jq("@comboitem:eq(60)")))
			.perform();
		sleep(1000);
		Assertions.assertNotEquals(0, jq(".z-combobox-popup").scrollTop());
	}
}
