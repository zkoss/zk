/* B100_ZK_5161Test.java

	Purpose:
		
	Description:
		
	History:
		5:14 PM 2023/12/11, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoAlertPresentException;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5161Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		try {
			driver.switchTo().alert();
			fail("Should not be here");
		} catch (NoAlertPresentException ex) {
			// ignore
		}
	}
}
