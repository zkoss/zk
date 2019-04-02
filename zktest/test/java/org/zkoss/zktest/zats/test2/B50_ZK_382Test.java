/* B50_ZK_382Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 11:45:14 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_ZK_382Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btn = jq("@button:contains(show/hide)");
		click(btn);
		WebDriverWait wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		Assert.assertEquals("[Widget]onHide btn", alert.getText());
		alert.accept();
		
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		alert = driver.switchTo().alert();
		Assert.assertEquals("[zWatch]onHide btn", alert.getText());
		alert.accept();
		
		click(btn);
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		alert = driver.switchTo().alert();
		Assert.assertEquals("[Widget]onShow btn", alert.getText());
		alert.accept();
		
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		alert = driver.switchTo().alert();
		Assert.assertEquals("[zWatch]onShow btn", alert.getText());
		alert.accept();
	}
}
