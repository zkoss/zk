/* B80_ZK_3339Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 30 10:15:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import java.time.Duration;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B80_ZK_3339Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		driver.manage().deleteAllCookies();
		refreshPage();
		waitResponse();

		final Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();
		assertThat(getZKLog(), allOf(containsString("invoked m0"), containsString("invoked m1")));

		refreshPage();
		waitResponse();
		final String zkLog = getZKLog();
		Assertions.assertEquals(2, StringUtils.countMatches(zkLog, "invoked m0"));
		Assertions.assertEquals(2, StringUtils.countMatches(zkLog, "invoked m1"));
	}

	private void refreshPage() {
		driver.navigate().refresh();
		new WebDriverWait(driver, Duration.ofSeconds(3))
				.until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
	}
}
