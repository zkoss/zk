/* B50_2934252Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 11:08:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2934252Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=th-TH")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "th-TH"));
	}

	@Test
	public void test() {
		connect();

		click(jq("@datebox > .z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-cell:contains(13)"));
		waitResponse();

		assertThat(jq("@datebox > input").val(), not(endsWith("/2000")));
	}
}
