/* B96_ZK_4835Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 30 14:23:07 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4835Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 5;

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/89.0.4389.90 Mobile/15E148 Safari/604.1");
		// fake as a mobile to have touch support desktop
	}

	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		final JQuery window = jq(".z-messagebox-window");
		final int oldLeft = window.positionLeft();
		dragdropTo(jq(".z-window-header-move"), 0, 0, 100, 0);
		Assertions.assertNotEquals(oldLeft, window.positionLeft(), "Mouse drag should be working");
	}

	@Test
	public void testResizable() {
		connect(getTestURL("B96-ZK-4835-2.zul"));

		final JQuery splitter = jq(".z-west-splitter");
		final int oldLeft = splitter.positionLeft();
		dragdropTo(splitter, 0, 0, 100, 0);
		Assertions.assertNotEquals(oldLeft, splitter.positionLeft(), "Mouse drag should be working");

		final JQuery header = jq("@listheader");
		final int oldWidth = header.outerWidth();
		dragdropTo(header, oldWidth / 2 - DRAG_THRESHOLD, 0, 100, 0);
		Assertions.assertNotEquals(oldWidth, header.outerWidth(), "Mouse drag should be working");
	}
}
