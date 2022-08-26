/* F96_ZK_4918Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 07 15:10:15 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class F96_ZK_4918Test extends WebDriverTestCase {
	@RegisterExtension
	public static ExternalZkXml CONFIG = new ExternalZkXml(F96_ZK_4918Test.class);

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		assertEquals("false", jq("$touchEnabled").text());

		final Widget win = widget("@window");
		final int oldPositionTop = jq(win).positionTop();
		final int oldPositionLeft = jq(win).positionLeft();
		dragdropTo(win.$n("cap"), 0, 0, 100, 100);
		assertEquals(oldPositionTop, jq(win).positionTop());
		assertEquals(oldPositionLeft, jq(win).positionLeft());
	}
}

