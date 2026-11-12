/* B110_ZK_6112_BiglistboxSelectedTest.java

	Purpose:
		Marble: a selected Biglistbox row is visibly marked, and shows a focus ring while moving with the arrow keys.

	Description:
		Marble's biglistbox.css styled only the hover state of a row, so a selected row looked like
		every other row. With the arrow keys the focus sits on the hidden .z-focus-a element, so the
		selected row is also the only sign of the keyboard position (WCAG 1.4.11, 2.4.7).

	History:
		Wed Nov 04 2026, Created by hawk

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6112_BiglistboxSelectedTest extends WebDriverTestCase {
	private static final String SELECTED_ROW = ".z-biglistbox-row.z-biglistbox-selected";
	private static final String TRANSPARENT = "rgba(0, 0, 0, 0)";
	private static final int ARROW_DOWN_PRESSES = 3;

	@BeforeEach
	public void marbleOnly() {
		Assumptions.assumeFalse("iceblue".equals(System.getProperty("zktest.theme")),
				"asserts Marble's row fill and focus ring; IceBlue paints the cells and draws no ring");
	}

	@Test
	public void clickPaintsTheSelectedRow() {
		connect();
		waitResponse();

		click(jq("$big").find(".z-biglistbox-row").eq(4).find("td").eq(0));
		waitResponse();
		getActions().moveToElement(toElement(jq("@label"))).perform();
		waitResponse();

		Assertions.assertTrue(selectedRow().exists(), "the clicked row is selected");
		Assertions.assertNotEquals(TRANSPARENT, selectedRow().css("background-color"),
				"a selected row must be filled, not look like an unselected row");
		Assertions.assertNotEquals(unselectedRow().css("background-color"), selectedRow().css("background-color"),
				"a selected row must differ from an unselected row");
	}

	@Test
	public void arrowKeysMoveAFocusRing() {
		connect();
		waitResponse();

		// Without za11y the Biglistbox has no Tab stop (its focus anchor is tabindex="-1"),
		// so focus it directly; the NO_A11Y suite variant would otherwise send the keys to body.
		eval("zk.$('$big').focus()");
		waitResponse();
		for (int i = 0; i < ARROW_DOWN_PRESSES; i++) {
			getActions().sendKeys(Keys.ARROW_DOWN).perform();
			waitResponse();
		}

		Assertions.assertTrue(selectedRow().exists(), "ArrowDown selects a row");
		Assertions.assertEquals("solid", selectedRow().css("outline-style"),
				"the row reached with the arrow keys must show a focus ring");
		Assertions.assertNotEquals(TRANSPARENT, selectedRow().css("background-color"),
				"the row reached with the arrow keys must be filled");
	}

	private JQuery selectedRow() {
		return jq("$big").find(SELECTED_ROW);
	}

	private JQuery unselectedRow() {
		return jq("$big").find(".z-biglistbox-row:not(.z-biglistbox-selected)").eq(0);
	}
}
