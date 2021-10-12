/* B36_3002536Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 11:34:11 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B36_3002536Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=fr-FR")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "fr-FR"));
	}

	@Test
	public void test() {
		connect();

		click(widget("@datebox").$n("btn"));
		waitResponse();
		click(widget("@calendar").$n("tm"));
		waitResponse(true);
		click(jq(".z-calendar-cell:contains(avr.)"));
		waitResponse(true);

		Assert.assertTrue(
				"the date should be selected",
				jq(".z-calendar-cell.z-calendar-selected").exists());
	}
}
