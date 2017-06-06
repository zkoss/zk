package org.zkoss.zktest.zats.test2;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.ClientWidget;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public abstract class F85_ZK_3681_TestCase extends WebDriverTestCase {
	@Override
	protected WebDriver getWebDriver() {
		if (driver == null) {
			// Vue.js doesn't support IE8 (Default setting)
			driver = new ZKWebDriver(BrowserVersion.INTERNET_EXPLORER_11, true);
		}
		return driver;
	}

	@Override
	protected void type(ClientWidget locator, String text) {
		focus(locator);
		WebElement webElement = toElement(locator);

		// Workaround: clear() triggers onchange
		// https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/214
		((HtmlUnitDriver) getWebDriver()).setJavascriptEnabled(false);
		webElement.clear();
		((HtmlUnitDriver) getWebDriver()).setJavascriptEnabled(true);

		webElement.sendKeys(text);
		// Workaround: sendKeys doesn't trigger onchange. Click other elements do.
		// http://advanceselenium.blogspot.com/2013/02/some-basic-question.html
		click(jq("body"));
	}

	@Override
	protected void waitResponse() {
		super.waitResponse();
		sleep(500); // Ensure Vue.js to be ready
	}
}
