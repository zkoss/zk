/* B96_ZK_5453Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 25 15:01:47 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.Duration;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_5453Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery chosenbox = jq("@chosenbox");
		click(chosenbox);
		waitResponse();
		JQuery input = chosenbox.find("input");
		String content = "'\"><img src=foo onerror=alert(/XSS-/+location)>";
		sendKeys(input, content, Keys.ENTER);
		assertFalse(showAlert());
		assertEquals(content, jq(".z-chosenbox-item-content").text());
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