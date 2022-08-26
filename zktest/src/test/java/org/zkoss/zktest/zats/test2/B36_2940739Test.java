/* B36_2940739Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 11:34:11 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@NotThreadSafe
public class B36_2940739Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=th-TH")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "th-TH"));
	}

	@Test
	public void test() {
		connect();

		try {
			String yearMonth1 = jq("@calendar .z-calendar-title").text();

			click(jq("@datebox:eq(0) > .z-datebox-button"));
			waitResponse();
			String yearMonth2 = jq(".z-datebox-popup.z-datebox-open .z-calendar-title").text();

			Assertions.assertEquals(yearMonth1, yearMonth2);

			click(jq("@datebox:eq(1) > .z-datebox-button"));
			waitResponse();
			String yearMonth3 = jq(".z-datebox-popup.z-datebox-open .z-calendar-title").text();

			Assertions.assertEquals(yearMonth1, yearMonth3);
		} finally {
			click(jq("body"));
			waitResponse();
			click(jq("@button"));
			waitResponse();
		}
	}
}
