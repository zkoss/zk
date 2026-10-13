/* F110_ZK_6084_MenuFocusTest.java

	Purpose:

	Description:

	History:
		Wed Sep 16 14:00:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A submenu row can take the focus and keeps the arrows reaching the menupopup, both in CE.
 * Handing the focus back to that row when a nested popup closes is za11y's Menupopup#focus_,
 * hence the tag; CE alone keeps focusing the menupopup.
 */
public class F110_ZK_6084_MenuFocusTest extends WebDriverTestCase {
	/**
	 * Id of the widget owning document.activeElement.
	 */
	private static final String FOCUSED_ROW = "(zk.Widget.$(document.activeElement) || {}).id || '(none)'";

	@Test
	public void arrowingOntoASubmenuMovesFocusOntoIt() {
		connect();
		waitResponse();

		// the click opens the popup without moving focus into it, so the second ArrowDown
		// is the one that lands on the submenu row
		click(jq("$top"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN).perform();
		waitResponse();

		assertEquals("sub", getEval(FOCUSED_ROW));
	}

	@Test
	@Tag("WcagTestOnly")
	public void closingANestedPopupMovesFocusOntoTheParentRow() {
		connect();
		waitResponse();

		click(jq("$top"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_RIGHT).perform();
		waitResponse();
		assertEquals("nested", getEval(FOCUSED_ROW));

		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();

		assertEquals("sub", getEval(FOCUSED_ROW));
		assertFalse(jq("$subpp").is(":visible"));
	}

	@Test
	public void arrowKeysReachTheMenupopupWhenAnAncestorHoldsCtrlKeys() {
		connect();
		waitResponse();

		click(jq("$top2"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN).perform();
		waitResponse();
		assertEquals("sub2", getEval(FOCUSED_ROW));

		// the row's onOK listener routes its keydown through afterKeyDown_, where the
		// ancestor ctrlKeys would otherwise consume the arrow
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();

		assertEquals("last2", getEval(FOCUSED_ROW));
	}
}
