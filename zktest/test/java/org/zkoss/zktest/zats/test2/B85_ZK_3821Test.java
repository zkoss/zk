/* B85_ZK_3821Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 18 12:42:38 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3821Test extends WebDriverTestCase {
	@Override
	protected WebDriver getWebDriver() {
		if (driver == null) {
			final String locale = "de-DE"; // German locale
			BrowserVersion browser = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.getDefault())
					.setSystemLanguage(locale)
					.setBrowserLanguage(locale)
					.setUserLanguage(locale)
					.build();
			driver = new ZKWebDriver(browser, true);
		}
		return driver;
	}

	@Test
	public void test() {
		connect();
		sleep(1500);

		click(jq("@button"));
		waitResponse();

		Assert.assertThat(
				"The message is still in English!",
				jq(".z-messagebox > @label").html(),
				not(startsWith("The resource you request")));
	}
}
