/* B110_ZK_6192Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Oct 19 14:44:56 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6192Test extends WebDriverTestCase {
	private static final String[] EDGE = {"edgeRange", "edgeMulti"};
	private static final Map<String, String> CLIP = Map.of("edgeMulti", "clip", "fixedMulti", "fixedClip",
			"scaledRange", "scaledClip", "scrollRange", "scrollClip", "scrollMulti", "scrollClip");

	@Test
	public void hoverTooltipsAvoidViewportAndAncestorClipping() {
		connect();
		for (String id : EDGE) {
			for (int i = 0; i < 2; i++) {
				hover(id, i);
				assertTooltipVisible(id, i);
			}
			assertTooltipSide(id, 1, true);
			assertEquals("true", getEval("document.documentElement.scrollWidth <= document.documentElement.clientWidth"),
					"showing a tooltip should not create horizontal document overflow");
		}
	}

	@Test
	public void fixedTooltipsAvoidClippingOnInitialRenderAndUpdate() {
		connect();
		for (String id : new String[] {"fixedRange", "fixedMulti"}) {
			assertTooltipVisible(id, 0);
			assertTooltipVisible(id, 1);
			assertTooltipSide(id, 1, true);
		}
		click(jq("$show"));
		waitResponse();
		for (String id : EDGE) {
			assertTooltipVisible(id, 0);
			assertTooltipVisible(id, 1);
			assertTooltipSide(id, 1, true);
		}
	}

	@Test
	public void hoverReturnsTooltipsToRightWhenSpaceBecomesAvailable() {
		connect();
		for (String id : EDGE) {
			hover(id, 1);
			assertTooltipSide(id, 1, true);
		}
		click(jq("$room"));
		waitResponse();
		for (String id : EDGE) {
			for (int i = 0; i < 2; i++) {
				hover(id, i);
				assertTooltipVisible(id, i);
				assertTooltipSide(id, i, false);
			}
		}
	}

	@Test
	public void tooltipStaysRightWhenLeftSideHasLessRoom() {
		connect();
		assertTooltipSide("narrowMulti", 1, false);
	}

	@Test
	public void bodyOverflowDoesNotFlipTooltips() {
		connect();
		click(jq("$room"));
		waitResponse();
		// body's overflow propagates to the viewport, so its own box clips nothing
		getEval("document.body.style.cssText = 'overflow:hidden;width:100px'");
		for (int i = 0; i < 2; i++) {
			hover("edgeRange", i);
			assertTooltipVisible("edgeRange", i);
			assertTooltipSide("edgeRange", i, false);
		}
	}

	@Test
	public void draggingKeepsTooltipsVisible() {
		connect();
		for (String id : EDGE) {
			hover(id, 1);
			// ZK-6192: dismiss the A11Y hover bridge before grabbing the knob beneath it.
			getActions().sendKeys(Keys.ESCAPE).clickAndHold().pause(Duration.ofMillis(100))
					.moveByOffset(0, 40).perform();
			try {
				waitResponse();
				assertTrue(Integer.parseInt(jq("$" + id + " .z-sliderbuttons-tooltip").eq(1).text()) > 500,
						id + " end handle should move while dragging");
				assertTooltipVisible(id, 1);
				assertTooltipSide(id, 1, true);
			} finally {
				getActions().release().perform();
				waitResponse();
			}
			hover(id, 1);
			assertTooltipVisible(id, 1);
			assertTooltipSide(id, 1, true);
		}
	}

	@Test
	public void leftTooltipRemainsHoverableAndDismissibleWithA11y() {
		connect();
		if (!Boolean.parseBoolean(getEval("!!window.za11y"))) return;
		for (String id : EDGE) {
			hover(id, 1);
			int gapOffset = Integer.parseInt(getEval(
					buttonScript(id, 1, "Math.round((t.right + r.left) / 2 - r.left - r.width / 2)")));
			getActions().moveByOffset(gapOffset, 0).perform();
			waitResponse();
			assertTooltipVisible(id, 1);
			getActions().moveToElement(toElement(jq("$" + id + " .z-sliderbuttons-tooltip").eq(1))).perform();
			waitResponse();
			assertTooltipVisible(id, 1);

			hover(id, 1);
			getActions().sendKeys(Keys.ESCAPE).moveByOffset(1, 0).perform();
			waitResponse();
			assertEquals("none", jq("$" + id + " .z-sliderbuttons-tooltip").eq(1).css("display"),
					"Escape should keep the tooltip hidden while its knob stays hovered");
			getActions().moveToElement(toElement(jq("$room"))).perform();
			waitResponse();
			hover(id, 1);
			assertTooltipVisible(id, 1);
		}
	}

	@Test
	public void scaledContainerKeepsTooltipsVisible() {
		connect();
		for (int i = 0; i < 2; i++) {
			assertTooltipVisible("scaledRange", i);
		}
		assertTooltipSide("scaledRange", 1, true);
	}

	@Test
	public void nativeAncestorScrollRepositionsFixedTooltips() {
		connect();
		for (String id : new String[] {"scrollRange", "scrollMulti"}) {
			assertTooltipVisible(id, 1);
			assertTooltipSide(id, 1, true);
		}
		getEval("jq('$scrollClip')[0].scrollLeft = 181");
		waitResponse();
		for (String id : new String[] {"scrollRange", "scrollMulti"}) {
			for (int i = 0; i < 2; i++) {
				assertTooltipVisible(id, i);
				assertTooltipSide(id, i, false);
			}
		}
		getEval("jq('$scrollClip')[0].scrollLeft = 0");
		waitResponse();
		for (String id : new String[] {"scrollRange", "scrollMulti"}) {
			assertTooltipVisible(id, 1);
			assertTooltipSide(id, 1, true);
		}
	}

	private void hover(String id, int index) {
		getActions().moveToElement(toElement(jq("$" + id + " .z-sliderbuttons-button").eq(index))).perform();
		waitResponse();
	}

	private void assertTooltipSide(String id, int index, boolean left) {
		assertEquals("true", getEval(buttonScript(id, index, left ? "t.right <= r.left" : "t.left >= r.right")),
				id + " tooltip " + index + " should appear on the " + (left ? "left" : "right"));
	}

	static String buttonScript(String id, int index, String expr) {
		return "(function (b) {var r = b.getBoundingClientRect(),"
				+ "t = b.querySelector('.z-sliderbuttons-tooltip').getBoundingClientRect();"
				+ "return " + expr + ";})(jq('$" + id + " .z-sliderbuttons-button')[" + index + "])";
	}

	private void assertTooltipVisible(String id, int index) {
		String boundary = CLIP.containsKey(id) ? "jq('$" + CLIP.get(id) + "')[0]" : "document.documentElement";
		assertEquals("true", getEval("(function (t, p) {var r = t.getBoundingClientRect(),"
				+ "b = p.getBoundingClientRect(), scale = p === document.documentElement ? 1 : b.width / p.offsetWidth,"
				+ "left = p === document.documentElement ? 0 : b.left + p.clientLeft * scale,"
				+ "right = left + p.clientWidth * scale, pe = t.style.pointerEvents;"
				// ZK-6192: a theme may give the tooltip pointer-events:none, which hides it from hit-testing
				+ "t.style.pointerEvents = 'auto';"
				+ "var painted = document.elementsFromPoint(r.left + r.width / 2, r.top + r.height / 2).indexOf(t) >= 0;"
				+ "t.style.pointerEvents = pe;"
				+ "return r.width > 0 && r.height > 0 && r.left >= left && r.right <= right && painted;"
				+ "})(jq('$" + id + " .z-sliderbuttons-tooltip')[" + index + "], " + boundary + ")"),
				id + " tooltip " + index + " should be painted inside its clipping boundary");
	}
}
