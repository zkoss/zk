/* B96_ZK_4780Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 05 12:17:40 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.DockerWebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4780Test extends DockerWebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=de-DE")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "de-DE"))
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		try {
			Assertions.assertEquals("0,5", jq("$db1").val());
			Assertions.assertEquals("0,5", jq("$db2 > .z-doublespinner-input").val());
			Assertions.assertEquals("0,5", jq("$db3").val());
		} finally {
			click(jq("@button"));
			waitResponse();
		}
	}
}
