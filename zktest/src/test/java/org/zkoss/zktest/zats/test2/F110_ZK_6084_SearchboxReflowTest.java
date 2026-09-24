/* F110_ZK_6084_SearchboxReflowTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 18:00:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.Reflow;

/**
 * WCAG 1.4.10 Reflow - https://www.w3.org/WAI/WCAG21/Understanding/reflow
 *
 * <p>{@code Searchbox.open()} freezes the popup's inline {@code min-width} to the control's width,
 * so a fluid control pins it near 1200px. CSS alone cannot undo that - the used width is
 * {@code max(min-width, min(max-width, width))} - so za11y clamps the inline value from an
 * augmented {@code _repositionPopup} as well as capping the popup. Opened at 1280px and only then
 * narrowed: opening at 320px would never freeze a wide min-width.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_SearchboxReflowTest extends WebDriverTestCase {
	private static final String POPUP = ".z-searchbox-popup";

	@AfterEach
	public void restoreViewport() {
		Reflow.clearViewport(getWebDriver());
	}

	@Test
	public void thePopupFollowsTheViewportDownAfterBeingOpenedWide() {
		connect();
		waitResponse();
		narrowTo(Reflow.REFERENCE_WIDTH, Reflow.REFERENCE_HEIGHT);

		click(jq("$myComp"));
		waitResponse(true);
		Reflow.awaitLaidOut(POPUP);
		long frozen = popupMinWidth();
		assertTrue(frozen > Reflow.WIDTH, "precondition: opening at "
				+ Reflow.REFERENCE_WIDTH + "px must freeze a min-width wider than a 320px "
				+ "viewport, was " + frozen);

		narrowTo(Reflow.WIDTH, Reflow.HEIGHT);

		long visible = Reflow.measure().visibleWidth();
		assertTrue(popupMinWidth() <= visible, "the clamp must bring the frozen min-width down to "
				+ "the viewport, was " + popupMinWidth() + " visible=" + visible);
		assertTrue(Reflow.rightOf(POPUP) <= visible + 1,
				"and the popup must fit, was " + Reflow.rectOf(POPUP));

		// CE's state: the cap gone and the min-width left where open() put it
		assertEquals(1, Reflow.removeDeclaration("max-width", POPUP),
				"expected za11y to cap " + POPUP);
		setPopupMinWidth(frozen);
		assertTrue(Reflow.rightOf(POPUP) > visible, "unclamped and uncapped, the popup must run "
				+ "past the right edge, was " + Reflow.rectOf(POPUP));
		assertTrue(Reflow.measure().overflow() > 0, "and widen the page: " + Reflow.measure());

	}

	private long popupMinWidth() {
		return Long.parseLong(getEval("(function () {"
				+ "  var n = document.querySelector('" + POPUP + "');"
				+ "  return n ? Math.round(parseFloat(n.style.minWidth) || 0) : -1;"
				+ "})()"));
	}

	private void setPopupMinWidth(long px) {
		getEval("(function () {"
				+ "  document.querySelector('" + POPUP + "').style.minWidth = '" + px + "px';"
				+ "  return 1;"
				+ "})()");
	}

	private void narrowTo(int width, int height) {
		WebDriver driver = getWebDriver();
		Assumptions.assumeTrue(Reflow.isSupported(driver),
				"a true " + width + "px CSS viewport needs a CDP device-metrics override");
		assertEquals(width, Reflow.setViewport(driver, width, height),
				"the CSS viewport did not reach " + width + "px");
		waitResponse(true);
	}
}
