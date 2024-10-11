/* B101_ZK_5766_2Test.java

	Purpose:

	Description:

	History:
		9:51 AM 2024/10/11, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoAlertPresentException;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5766_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-menuitem"));
		try {
			driver.switchTo().alert();
			fail("Should not be here");
		} catch (NoAlertPresentException ex) {
			// ignore
		}
	}
}
