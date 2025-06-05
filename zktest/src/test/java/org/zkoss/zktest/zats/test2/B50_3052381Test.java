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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.DockerWebDriverTestCase;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class B50_3052381Test extends DockerWebDriverTestCase {
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
		String[] italianWeekdayNames = {"lun", "mar", "mer", "gio", "ven", "sab", "dom"};

		int weekIndexStart = -1;
		JQuery weeks = jq("@calendar th");
		String[] lines = getZKLog().split("\n");
		for (int i = 0; i < weeks.length(); ++i) {
			if (lines[0].startsWith(weeks.eq(i).text())) {
				weekIndexStart = i;
				break;
			}
		}
		if (weekIndexStart == -1) // should be 0 ~ 6
			fail("No week index found");
		for (int i = 0, lineLen = lines.length; i < lineLen; i++) {
			int index = (weekIndexStart + i) % 7;
			String week = weeks.eq(index).text();
			assertThat(lines[i], startsWithIgnoringCase(week));
			assertEquals(week, italianWeekdayNames[index]);
		}
	}
}
