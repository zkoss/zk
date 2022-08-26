/* B50_2947657Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 18:10:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2947657Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		type(jq("@textbox"), "ZK");
		type(jq("@combobox input"), "Engineer");
		click(jq("@listitem:eq(3)"));
		driver.findElement(By.cssSelector("input[type='submit']")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
		waitResponse();

		JQuery labels = jq("@div @label");
		Assertions.assertEquals("Engineer", labels.eq(1).html());
		Assertions.assertEquals("ZK", labels.eq(3).html());
		Assertions.assertEquals("4", labels.eq(5).html());
	}
}
