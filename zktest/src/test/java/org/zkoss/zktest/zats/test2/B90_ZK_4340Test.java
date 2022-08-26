/* B90_ZK_4340Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 29 16:30:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Duration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B90_ZK_4340Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		final JQuery textbox = jq("@textbox");
		new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.presenceOfElementLocated(textbox));
		Assertions.assertTrue(textbox.exists());

		click(textbox);
		sendKeys(textbox, "test");
		click(jq("body"));
		waitResponse();

		assertThat(getZKLog(), Matchers.startsWith("[InputEvent onChange"));
	}
}
