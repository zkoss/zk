/* F110_ZK_6084_HighlightedWindowResizeTest.java

	Purpose:

	Description:

	History:
		Tue Oct 06 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A highlighted window is placed like a modal one, so a browser resize places it again, as
 * B70-ZK-2892 does for a modal window.
 */
public class F110_ZK_6084_HighlightedWindowResizeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		resizeTo(1280);
		click(jq("@button"));
		waitResponse();

		resizeTo(640);

		JQuery win = jq("$win");
		int viewport = Integer.parseInt(getEval("document.documentElement.clientWidth"));
		Assertions.assertEquals(viewport, win.offsetLeft() * 2 + win.outerWidth(), 2,
				"the window must be centered again in the narrower browser");
		// Window.ts _updDomPos caps the top of a window placed without a position at 100px
		Assertions.assertTrue(win.offsetTop() <= 100,
				"and must stay near the top, was " + win.offsetTop());
	}

	private void resizeTo(int width) {
		driver.manage().window().setSize(new Dimension(width, 1024));
		// zk debounces the browser resize and only then fires the onSize watch
		sleep(500);
		waitResponse();
	}
}
