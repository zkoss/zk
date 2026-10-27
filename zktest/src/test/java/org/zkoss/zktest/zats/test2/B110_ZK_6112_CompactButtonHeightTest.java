/* B110_ZK_6112_CompactButtonHeightTest.java

	Purpose:

	Description:

	History:
		Wed Oct 14 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Marble: under data-density="compact" every button variant is --zk-button-height tall
 * (the outlined 1px border and the confirmpopup/messagebox buttons included), the icon-only
 * button is --zk-icon-button-size, and the default density keeps its heights (ZK-6112).
 */
public class B110_ZK_6112_CompactButtonHeightTest extends WebDriverTestCase {
	private static final String[] BUTTONS = {"$filled", "$outlined", "$text", "$iconLabel"};
	private static final String CONFIRMPOPUP_BUTTONS = ".z-confirmpopup-ok, .z-confirmpopup-cancel";
	private static final String MESSAGEBOX_BUTTONS = ".z-messagebox-window .z-button";

	@Test
	public void testDefaultDensityKeepsItsHeights() {
		connect();
		waitResponse();
		assertButtonHeights(36, 40);
	}

	@Test
	public void testCompactDensityUsesTheButtonHeightToken() {
		connect();
		waitResponse();
		getEval("document.documentElement.setAttribute('data-density', 'compact')");
		assertEquals("30px", rootToken("--zk-button-height"));
		assertEquals("32px", rootToken("--zk-icon-button-size"));
		assertButtonHeights(30, 32);
	}

	@Test
	public void testComfortableRegionInsideCompactRootRestoresTheDefaults() {
		connect();
		waitResponse();
		getEval("document.documentElement.setAttribute('data-density', 'compact')");
		getEval("zk.Widget.$('$region').$n().setAttribute('data-density', 'comfortable')");
		for (String button : BUTTONS)
			assertEquals(36, height(button), button);
		assertEquals(40, height("$iconOnly"), "$iconOnly");
	}

	private void assertButtonHeights(int buttonHeight, int iconButtonHeight) {
		for (String button : BUTTONS)
			assertEquals(buttonHeight, height(button), button);
		assertEquals(iconButtonHeight, height("$iconOnly"), "$iconOnly");

		click(jq("$openConfirmpopup"));
		waitResponse();
		assertEquals(buttonHeight, height(".z-confirmpopup-ok"), "confirmpopup OK");
		assertEquals(buttonHeight, height(".z-confirmpopup-cancel"), "confirmpopup Cancel");
		click(jq(".z-confirmpopup-cancel"));
		waitResponse();

		click(jq("$openMessagebox"));
		waitResponse();
		assertEquals(2, jq(MESSAGEBOX_BUTTONS).length());
		for (int i = 0; i < 2; i++)
			assertEquals(buttonHeight, jq(MESSAGEBOX_BUTTONS).eq(i).outerHeight(), "messagebox button " + i);
	}

	private int height(String selector) {
		return jq(selector).outerHeight();
	}

	private static String rootToken(String name) {
		return getEval("getComputedStyle(document.documentElement).getPropertyValue('" + name + "').trim()");
	}
}
