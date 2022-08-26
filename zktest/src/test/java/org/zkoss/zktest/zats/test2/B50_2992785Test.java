/* B50_2992785Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 11:54:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2992785Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@row:eq(0) a"));
		checkAlert();
		click(jq("@row:eq(1) a"));
		checkAlert();
		click(jq("@row:eq(2) a"));
		checkAlert();
		click(jq("@row:eq(3) a"));
		checkAlert();
	}

	private void checkAlert() {
		Alert alert = driver.switchTo().alert();
		String id = alert.getText();
		alert.accept();
		assertThat(id, matchesRegex("\\w+"));
	}
}
