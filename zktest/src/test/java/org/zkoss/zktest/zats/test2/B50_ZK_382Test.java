/* B50_ZK_382Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 11:45:14 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_382Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btn = jq("@button:contains(show/hide)");
		click(btn);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(7));
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		Assertions.assertEquals("[Widget]onHide btn", alert.getText());
		alert.accept();
		
		wait = new WebDriverWait(driver, Duration.ofSeconds(7));
		wait.until(ExpectedConditions.alertIsPresent());
		alert = driver.switchTo().alert();
		Assertions.assertEquals("[zWatch]onHide btn", alert.getText());
		alert.accept();
		
		click(btn);
		wait = new WebDriverWait(driver, Duration.ofSeconds(7));
		wait.until(ExpectedConditions.alertIsPresent());
		alert = driver.switchTo().alert();
		Assertions.assertEquals("[Widget]onShow btn", alert.getText());
		alert.accept();
		
		wait = new WebDriverWait(driver, Duration.ofSeconds(7));
		wait.until(ExpectedConditions.alertIsPresent());
		alert = driver.switchTo().alert();
		Assertions.assertEquals("[zWatch]onShow btn", alert.getText());
		alert.accept();
	}
}
