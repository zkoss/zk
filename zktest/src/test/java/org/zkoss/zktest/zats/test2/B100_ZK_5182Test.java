/* B100_ZK_5182Test.java

	Purpose:
		
	Description:
		
	History:
		5:11 PM 2023/12/27, Created by jumperchen

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
public class B100_ZK_5182Test extends WebDriverTestCase {
	@Test
	public void testXSS() {
		connect();
		try {
			driver.switchTo().alert();
			fail("Should not be here");
		} catch (NoAlertPresentException e)  {
			// ignore
		}
	}
	@Test
	public void testSetContentXSS() {
		connect("/test2/B100-ZK-5182-1.zul");
		try {
			driver.switchTo().alert();
			fail("Should not be here");
		} catch (NoAlertPresentException e)  {
			// ignore
		}
	}
}
