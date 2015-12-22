/* B80_ZK_2888Test.java

	Purpose:
		
	Description:
		
	History:
		3:20 PM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2888Test extends WebDriverTestCase {
	protected WebDriver getWebDriver() {
		if (driver == null) {
			BrowserVersion chrome = BrowserVersion.CHROME;
			chrome.setUserAgent("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
			ZKWebDriver zdriver = new ZKWebDriver(chrome);
			zdriver.setJavascriptEnabled(true);
			driver = zdriver;
		}
		return driver;
	}

	@Test
	public void testZK2888() {
			connect();
			waitResponse();
			assertEquals(
					"You should see a number below. (iphone with chrome only)7",
					trim(jq("@label").text()));
	}
}
