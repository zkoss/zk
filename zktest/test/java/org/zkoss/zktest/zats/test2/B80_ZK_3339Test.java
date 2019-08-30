/* B80_ZK_3339Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 30 10:15:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertThat(getZKLog(), allOf(containsString("invoked m0"), containsString("invoked m1")));

		refreshPage();
		waitResponse();
		final String zkLog = getZKLog();
		Assert.assertEquals(2, StringUtils.countMatches(zkLog, "invoked m0"));
		Assert.assertEquals(2, StringUtils.countMatches(zkLog, "invoked m1"));
	}

	private void refreshPage() {
		driver.navigate().refresh();
		new WebDriverWait(driver, 3)
				.until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
	}
}
