/* B50_3009925Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 14:22:56 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3009925Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().accept();
	}
}
