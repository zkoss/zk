/* F50_3291332Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 14:52:24 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3291332Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		driver.navigate().refresh(); // trigger rmDesktop
		sleep(1000);
		driver.navigate().refresh(); // the beacon of rmDesktop is asynchronous, so wait for 1s and refresh
		new WebDriverWait(driver, Duration.ofSeconds(3))
				.until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
		waitResponse();
		assertThat(jq("@button").text(), startsWith("rmDesktop received at"));
	}
}
