/* B95_ZK_4742_1Test.java

		Purpose:

		Description:

		History:
				Wed Dec 30 16:27:44 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import java.time.Duration;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4742_1Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect("/test2/B95-ZK-4742.zul");
		click(jq("@button"));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		Alert alert = wait.until(ExpectedConditions.alertIsPresent());
		MatcherAssert.assertThat(alert.getText(), containsString("(403: Forbidden)"));
		alert.accept();
	}
}
