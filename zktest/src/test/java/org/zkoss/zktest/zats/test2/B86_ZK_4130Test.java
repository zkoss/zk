/* B86_ZK_4130Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 10 16:56:08 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4130Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-menu"));
		waitResponse();
		verifyExist(jq(".z-menupopup-open"));

		JQuery openMenupopup = findButtonByContent("open menupopup");
		click(openMenupopup);
		waitResponse();
		verifyExist(jq(".z-menupopup-open"));
		rightClick(openMenupopup);
		waitResponse();
		verifyExist(jq(".z-menupopup-open"));

		JQuery openPopup = findButtonByContent("open popup");
		click(openPopup);
		waitResponse();
		verifyExist(jq(".z-popup-open"));
		rightClick(openPopup);
		waitResponse();
		verifyExist(jq(".z-popup-open"));

		click(findButtonByContent("showNotification"));
		waitResponse();
		verifyExist(jq(".z-notification-open"));
	}

	private void verifyExist(JQuery popup) {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 200)");
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
		Assertions.assertTrue(popup.exists());
		click(findButtonByContent("close popup"));
		waitResponse();
	}

	private JQuery findButtonByContent(String content) {
		return jq(".z-button:contains(" + content + ")");
	}

}
