/* B110_ZK_6112_PortallayoutPopupFocusTest.java

	Purpose:
		Marble with za11y: the keyboard-focused item of the Portallayout move popup has a focus ring.

	Description:
		za11y's Marble CSS removed the outline of the popup's items and marked focus only with a
		12% state layer, 1.27:1 on the white popup (WCAG 1.4.11, 2.4.7).

	History:
		Wed Nov 04 2026, Created by hawk

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class B110_ZK_6112_PortallayoutPopupFocusTest extends WebDriverTestCase {
	private static final String POPUP = ".z-portallayout-popup-open";

	@Test
	public void focusedItemHasAFocusRing() {
		connect();
		sendKeys(jq("$translate"), Keys.SPACE);
		waitResponse();
		Assertions.assertTrue(jq(POPUP).exists(), "precondition: Space opens the move popup");

		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();

		JQuery focused = jq(".z-portallayout-popup-nav:focus");
		Assertions.assertEquals("Doing", focused.text(), "precondition: ArrowDown focuses the next column");
		Assertions.assertEquals("solid", focused.css("outline-style"),
				"the focused item must have a focus ring, not only a grey fill");
	}
}
