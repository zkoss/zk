/* F50_2142143Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 14:13:06 CST 2019, Created by rudyhuang

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

/**
 * @author rudyhuang
 */
public class F50_2142143Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
		Assertions.assertEquals("checkbox", alert.getText());
	}
}
