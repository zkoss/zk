/* B102_ZK_5612Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 21 17:52:16 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5612Test extends WebDriverTestCase {
	@Test
	public void testSerbianInTopCombobox() {
		testCombobox(0, "serbian");
	}

	@Test
	public void testSerbianInMiddleCombobox() {
		testCombobox(1, "serbian");
	}

	@Test
	public void testSerbianInBottomCombobox() {
		testCombobox(2, "serbian");
	}

	@Test
	public void testSpanishInTopCombobox() {
		testCombobox(0, "spanish");
	}

	@Test
	public void testSpanishInMiddleCombobox() {
		testCombobox(1, "spanish");
	}

	@Test
	public void testSpanishInBottomCombobox() {
		testCombobox(2, "spanish");
	}

	private void testCombobox(int index, String s) {
		connect();
		JQuery combobox = jq("@combobox").eq(index);
		click(combobox);
		waitResponse();
		JQuery popup = jq(".z-combobox-popup.z-combobox-open");
		for (char c : s.toCharArray()) {
			getActions().sendKeys(String.valueOf(c)).perform();
			waitResponse();
			int comboboxLeft = combobox.offsetLeft();
			int comboboxRight = comboboxLeft + combobox.outerWidth();
			int comboboxTop = combobox.offsetTop();
			int comboboxBottom = comboboxTop + combobox.outerHeight();
			int popupWidth = popup.outerWidth();
			int popupLeft = popup.offsetLeft();
			int popupRight = popupLeft + popupWidth;
			int popupHeight = popup.outerHeight();
			int popupTop = popup.offsetTop();
			int popupBottom = popupTop + popupHeight;
			int windowRight = Integer.parseInt(getEval("jq(window).width()"));
			int windowBottom = Integer.parseInt(getEval("jq(window).height()"));

			int delta = 2;
			// check the page has neither horizontal nor vertical scrollbar
			assertEquals(Integer.parseInt(getEval("jq(document).width()")), windowRight, delta);
			assertEquals(Integer.parseInt(getEval("jq(document).height()")), windowBottom, delta);
			// check the popup is horizontally aligned to the combobox
			if (comboboxLeft + popupWidth < windowRight) {
				assertEquals(comboboxLeft, popupLeft, delta);
			} else {
				assertEquals(comboboxRight, popupRight, delta);
			}
			// check the popup is vertically aligned to the combobox
			if (comboboxBottom + popupHeight < windowBottom) {
				assertEquals(comboboxBottom, popupTop, delta);
			} else {
				assertEquals(comboboxTop, popupBottom, delta);
			}
		}
	}
}
