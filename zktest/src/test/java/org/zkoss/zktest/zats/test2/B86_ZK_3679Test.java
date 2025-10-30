/* B86_ZK_3679Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 12 15:55:36 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_3679Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("html")));

		try {
			driver.findElement(By.xpath("//*[contains(text(), 'org.zkoss.zk.ui.OperationException: ')]"));
			Assertions.fail("Shouldn't appear 'OperationException'");
		} catch (NoSuchElementException ignored) { }

		String title = jq("@window > .z-window-header").text().trim();
		String content = jq("@window > .z-window-content").text().trim();
		assertEquals("Custom Error Handler: Error 500", title);
		assertThat(content, containsString("org.zkoss.zktest.test2.B86_ZK_3679Exception"));
	}
}
