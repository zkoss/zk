package org.zkoss.zktest.zats.test2;

import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public abstract class F85_ZK_3681_TestCase extends WebDriverTestCase {
	public WebDriver connect() {
		WebDriver connect = super.connect();
		waitResponse();
		return connect;
	}
}
