/* B90_ZK_4385Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 10 18:48:38 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4385Test extends WebDriverTestCase {
	@Test(expected = TimeoutException.class)
	public void test() {
		connect();
		Actions actions = getActions();

		actions.pause(1000)
				.click(toElement(jq("@textbox")))
				.perform();
		waitForDialog();

		actions.click(toElement(jq("@button:contains(Cancel)")))
				.pause(100)
				.perform();
		waitForDialog();
	}

	private void waitForDialog() {
		new WebDriverWait(getWebDriver(), 3)
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Cancel']")));
	}
}
