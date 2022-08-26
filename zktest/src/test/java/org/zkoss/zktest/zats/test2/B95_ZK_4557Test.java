/* B95_ZK_4557Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 27 17:56:34 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4557Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		Assertions.assertFalse(checkAlert(),
				"should not present any browser alert");
	}
	
	public boolean checkAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
