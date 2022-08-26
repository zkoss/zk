/* B70_ZK_2477Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 12:12:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2477Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=ro-RO") // Romanian (Romania)
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "ro-RO"));
	}

	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertEquals("ro_RO\nen", getZKLog());

		click(widget("@datebox").$n("btn"));
		waitResponse();

		LocalDate now = LocalDate.now();
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		String weekdayFull = dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.US);
		String weekdayShort = dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.US);

		int weekdayIndex = Integer.parseInt(jq("@calendar .z-calendar-selected").eval("index()"));
		String weekdayLabel = jq("@calendar th").eq(weekdayIndex).text();

		assertThat(jq(widget("@datebox").$n("real")).val(), startsWith(weekdayFull));
		Assertions.assertEquals(weekdayLabel, weekdayShort);
	}
}
