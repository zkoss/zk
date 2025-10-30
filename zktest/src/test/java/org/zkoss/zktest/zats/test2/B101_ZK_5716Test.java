/* B101_ZK_5716Test.java

	Purpose:

	Description:

	History:
		4:17 PM 2024/9/10, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.ExternalWebXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
@ForkJVMTestOnly
public class B101_ZK_5716Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalWebXml CONFIG = new ExternalWebXml(B101_ZK_5716Test.class);

	@Test
	public void test() {
		connect();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 10 seconds timeout

		click(jq(".z-error .button:eq(1)"));
		assertNoJSError();
		wait.until(d -> jq(".z-error").isVisible());

		click(jq(".z-error .button:eq(0)"));
		waitResponse();
		assertNoJSError();
		assertFalse(jq(".z-error").isVisible());

		click(jq(".z-log .z-button"));
		waitResponse();
		assertNoJSError();
		assertFalse(jq(".z-log").isVisible());
	}
}
