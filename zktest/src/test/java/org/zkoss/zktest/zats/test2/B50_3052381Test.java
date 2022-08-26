/* B50_3052381Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 26 16:03:07 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWithIgnoringCase;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_3052381Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=it-IT")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "it-IT"));
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		int weekIndexStart = Integer.parseInt(jq("@calendar .z-calendar-selected").eval("index()"));
		JQuery weeks = jq("@calendar th");
		String[] lines = getZKLog().split("\n");
		for (int i = 0, lineLen = lines.length; i < lineLen; i++) {
			String week = weeks.eq((weekIndexStart + i) % 7).text();
			assertThat(lines[i], startsWithIgnoringCase(week));
		}
	}
}
