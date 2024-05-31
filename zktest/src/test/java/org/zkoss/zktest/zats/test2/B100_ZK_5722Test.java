/* B100_ZK_5722Test.java

	Purpose:
		
	Description:
		
	History:
		9:10 AM 2024/5/31, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5722Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@a"));
		assertFalse(showAlert(), "XSS attack detected");
		click(jq("@button"));
		assertFalse(showAlert(), "XSS attack detected");

	}
	public boolean showAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
