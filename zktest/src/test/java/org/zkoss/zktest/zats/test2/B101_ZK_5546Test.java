/* B101_ZK_5546Test.java

	Purpose:

	Description:

	History:
		2:15 PM 2024/10/7, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WindowType;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
@ForkJVMTestOnly
public class B101_ZK_5546Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B101_ZK_5546Test.class);

	@Test
	public void test() {
		connect();
		waitResponse();
		driver.switchTo().newWindow(WindowType.TAB);
		driver.get(getAddress() + "/test2/B101-ZK-5546.zul");
		Object[] windowHandles = driver.getWindowHandles().toArray();
		driver.switchTo().window((String) windowHandles[0]);
		click(jq("@button"));
		waitResponse();
		assertThat(driver.getCurrentUrl(), endsWith("timeout.zul"));
	}
}
