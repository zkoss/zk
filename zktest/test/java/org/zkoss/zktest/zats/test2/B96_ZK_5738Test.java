/* B96_ZK_5722Test.java

	Purpose:
		
	Description:
		
	History:
		9:10 AM 2024/5/31, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;

import java.time.Duration;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5738Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@a"));
		assertFalse("XSS attack detected", showAlert());
		click(jq("@button"));
		assertFalse("XSS attack detected", showAlert());

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
