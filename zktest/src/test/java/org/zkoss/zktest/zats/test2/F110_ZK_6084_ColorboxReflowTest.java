/* F110_ZK_6084_ColorboxReflowTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 16:00:00 CST 2026, Created by peggypeng

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
 * <p>The palette (340px) and picker (310px) exceed a 320px viewport and CE caps neither.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_ColorboxReflowTest extends WebDriverTestCase {
	private static final String POPUP = ".z-colorbox-popup";

	@AfterEach
	public void restoreViewport() {
		Reflow.clearViewport(getWebDriver());
	}

	@Test
	public void theCapKeepsThePaletteInsideTheViewport() {
		connect();
		waitResponse();
		narrowToReflowViewport();

		click(jq("@colorbox"));
		waitResponse(true);
		Reflow.awaitLaidOut(POPUP);
		assertCappedThenUncapped("palette");
	}

	@Test
	public void theCapKeepsThePickerInsideTheViewport() {
		connect();
		waitResponse();
		narrowToReflowViewport();

		click(jq("@colorbox"));
		waitResponse(true);
		Reflow.awaitLaidOut(POPUP);
		click(jq(".z-colorbox-pickericon"));
		waitResponse(true);
		Reflow.awaitLaidOut(POPUP);
		assertCappedThenUncapped("picker");
	}

	private void assertCappedThenUncapped(String state) {
		long visible = Reflow.measure().visibleWidth();

		assertTrue(Reflow.rightOf(POPUP) <= visible + 1, "the capped " + state
				+ " popup must stay inside the viewport, was " + Reflow.rectOf(POPUP)
				+ " visible=" + visible);
		assertEquals("auto", Reflow.computed(POPUP, "overflow-x"),
				"the cap is only safe because the popup scrolls inside - the grid must stay reachable");

		assertEquals(1, Reflow.removeDeclaration("max-width", POPUP),
				"expected za11y to cap " + POPUP);
		assertTrue(Reflow.rightOf(POPUP) > visible, "uncapped, the " + state
				+ " popup must extend past the right edge, was " + Reflow.rectOf(POPUP));
		assertTrue(Reflow.measure().overflow() > 0,
				"and must widen the page: " + Reflow.measure());
	}

	private void narrowToReflowViewport() {
		WebDriver driver = getWebDriver();
		Assumptions.assumeTrue(Reflow.isSupported(driver),
				"a true " + Reflow.WIDTH + "px CSS viewport needs a CDP device-metrics override");
		assertEquals(Reflow.WIDTH, Reflow.setViewport(driver, Reflow.WIDTH, Reflow.HEIGHT),
				"the CSS viewport did not reach " + Reflow.WIDTH + "px");
		waitResponse(true);
	}
}
