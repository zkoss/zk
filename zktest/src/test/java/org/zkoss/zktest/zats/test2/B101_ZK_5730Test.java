/* B101_ZK_5730Test.java

	Purpose:

	Description:

	History:
		12:11 PM 2024/10/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WindowType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5730Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		driver.switchTo().newWindow(WindowType.TAB);
		driver.get(getAddress() + "/test2/B101-ZK-5730.zul");
		waitResponse();
		Object[] windowHandles = driver.getWindowHandles().toArray();
		driver.switchTo().window((String) windowHandles[0]);
		click(jq("$op2"));
		driver.switchTo().window((String) windowHandles[1]);
		click(jq("$logout"));
		driver.switchTo().window((String) windowHandles[0]);
		waitResponse();
		assertNoZKError();
	}

	@Test
	public void testCase1() {
		connect("/test2/B101-ZK-5730-1.zul");
		click(jq("@button"));
		waitResponse();
		assertNoZKError();
	}
}
