/* B110_ZK_6196Test.java

        Purpose:
                
        Description:
                
        History:
                Sun Oct 18 18:08:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6196Test extends WebDriverTestCase {
	private static final String[] POSITIONS = {
			"bottom_right", "bottom_center", "bottom_left",
			"middle_right", "middle_center", "middle_left",
			"top_right", "top_center", "top_left"
	};

	@Test
	public void notificationUsesViewport() {
		assertViewportPositions("$notification", ".z-notification");
	}

	@Test
	public void popupUsesViewport() {
		assertViewportPositions("$openPopup", "$popup");
	}

	@Test
	public void referenceAndCoordinatesKeepTheirPositions() {
		connect();
		for (int[] scroll : new int[][] {{0, 0}, {200, 300}}) {
			scrollTo(scroll[0], scroll[1]);
			click(jq("$reference"));
			waitResponse();
			assertEquals(rect("$reference", "left"), rect("$popup", "left"), 1,
					"the popup must remain aligned with its reference");
			assertEquals(rect("$reference", "bottom"), rect("$popup", "top"), 1,
					"the popup must open below its reference");
			assertPainted("$popup");
			click(jq("$instructions"));
			waitResponse();

			click(jq("$coordinates"));
			waitResponse();
			assertEquals(600 - scroll[0], rect("$popup", "left"), 1,
					"explicit x must remain a document coordinate");
			assertEquals(500 - scroll[1], rect("$popup", "top"), 1,
					"explicit y must remain a document coordinate");
			assertPainted("$popup");
			click(jq("$closePopup"));
			waitResponse();
		}
	}

	private void assertViewportPositions(String button, String selector) {
		connect();
		Select positions = new Select(toElement(jq("$position")));
		for (int[] scroll : new int[][] {{0, 0}, {200, 300}}) {
			scrollTo(scroll[0], scroll[1]);
			for (String position : POSITIONS) {
				positions.selectByVisibleText(position);
				waitResponse();
				assertEquals(position, positions.getFirstSelectedOption().getText(), "selected position");
				click(jq(button));
				waitResponse(true);
				assertTrue(jq(selector).isVisible(), position + " must open");
				double width = number("document.documentElement.clientWidth");
				double height = number("document.documentElement.clientHeight");
				double x = position.endsWith("left") ? 0
						: (width - rect(selector, "width")) / (position.endsWith("center") ? 2 : 1);
				double y = position.startsWith("top") ? 0
						: (height - rect(selector, "height")) / (position.startsWith("middle") ? 2 : 1);
				String context = position + " at scroll (" + scroll[0] + ", " + scroll[1] + ")";
				assertEquals(x, rect(selector, "left"), 1, context + " horizontal alignment");
				assertEquals(y, rect(selector, "top"), 1, context + " vertical alignment");
				assertPainted(selector);
				click(jq(".z-notification".equals(selector) ? ".z-notification-close" : "$closePopup"));
				waitResponse(true);
			}
		}
	}

	private void scrollTo(int x, int y) {
		eval("window.scrollTo(" + x + ", " + y + ")");
		waitResponse();
		assertEquals(x, number("window.scrollX"), 1, "horizontal scroll precondition");
		assertEquals(y, number("window.scrollY"), 1, "vertical scroll precondition");
	}

	private void assertPainted(String selector) {
		assertTrue(Boolean.parseBoolean(getEval("(function () { var n = jq('" + selector + "')[0], "
				+ "r = n.getBoundingClientRect(); return n.contains(document.elementFromPoint("
				+ "r.left + r.width / 2, r.top + r.height / 2)); })()")),
				"the popup must be visible at its center");
	}

	private double rect(String selector, String side) {
		return number("jq('" + selector + "')[0].getBoundingClientRect()." + side);
	}

	private double number(String script) {
		return Double.parseDouble(getEval(script));
	}
}
