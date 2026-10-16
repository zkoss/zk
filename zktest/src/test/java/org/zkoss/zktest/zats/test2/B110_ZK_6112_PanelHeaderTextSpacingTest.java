/* B110_ZK_6112_PanelHeaderTextSpacingTest.java

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
 * Marble: under the WCAG 1.4.12 text spacing (line-height 1.5, letter-spacing .12em,
 * word-spacing .16em) a panel header keeps its icons inside the panel instead of pushing
 * them out; without it the header keeps its single line. A window header has the same
 * structure and the same defect, so it is checked too (ZK-6112).
 */
public class B110_ZK_6112_PanelHeaderTextSpacingTest extends WebDriverTestCase {
	private static final String[] PANELS = {"rounded", "normal", "none"};
	private static final String WINDOW = "window";
	/** 2 x --zk-header-padding-y (16px) + the 28px control buttons. */
	private static final int SINGLE_LINE_HEADER_HEIGHT = 60;
	private static final String TEXT_SPACING = "var s = document.createElement('style');"
			+ " s.textContent = '* { line-height: 1.5 !important; letter-spacing: .12em !important;"
			+ " word-spacing: .16em !important }';"
			+ " document.head.appendChild(s)";

	@Test
	public void testHeaderStaysOneLineWithoutTextSpacing() {
		connect();
		waitResponse();
		assertEquals(SINGLE_LINE_HEADER_HEIGHT, jq("$normal .z-panel-header").outerHeight());
		assertEquals(SINGLE_LINE_HEADER_HEIGHT, jq("$wide .z-panel-header").outerHeight());
		for (String panel : PANELS)
			assertEquals(0, iconsOutside(panel, "panel", 4), panel + ": icons outside the panel");
	}

	@Test
	public void testIconsStayInsideThePanelUnderTextSpacing() {
		connect();
		waitResponse();
		getEval("(function () {" + TEXT_SPACING + "; return 1; })()");
		for (String panel : PANELS)
			assertEquals(0, iconsOutside(panel, "panel", 4), panel + ": icons outside the panel");
		assertEquals(0, iconsOutside(WINDOW, "window", 3), "window: icons outside the window");
	}

	/**
	 * How many of the header icons of a panel or window ({@code kind}) lie (even partly)
	 * outside its box; -1 when it does not have the expected number of icons.
	 */
	private int iconsOutside(String id, String kind, int expectedIcons) {
		return Integer.parseInt(getEval("(function () {"
				+ " var box = zk.Widget.$('$" + id + "').$n(),"
				+ " bounds = box.getBoundingClientRect(),"
				+ " icons = box.querySelectorAll('.z-" + kind + "-icon'),"
				+ " outside = 0;"
				+ " for (var i = 0; i < icons.length; i++) {"
				+ "   var r = icons[i].getBoundingClientRect();"
				+ "   if (r.left < bounds.left - 0.5 || r.right > bounds.right + 0.5) outside++;"
				+ " }"
				+ " return icons.length === " + expectedIcons + " ? outside : -1; })()"));
	}
}
