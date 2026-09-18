/* F110_ZK_6084_CascaderReflowTest.java

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
 * <p>Two side-by-side caves ({@code min-width: 148px} each) exceed a 320px viewport. za11y caps
 * the popup and lets it scroll inside - column navigation is a genuinely 2-D region, which 1.4.10
 * permits. The page reuses {@code F90_ZK_4392Model} because its long label is half the cause.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_CascaderReflowTest extends WebDriverTestCase {
	private static final String POPUP = ".z-cascader-popup";

	@AfterEach
	public void restoreViewport() {
		Reflow.clearViewport(getWebDriver());
	}

	/**
	 * The popup fits; with the cap stripped back out at run time - CE's CSS exactly - it does not.
	 * The second half is what stops the first passing vacuously.
	 */
	@Test
	public void theCapKeepsThePopupInsideTheViewport() {
		connect();
		waitResponse();
		narrowToReflowViewport();

		click(jq("@cascader"));
		waitResponse(true);
		Reflow.awaitLaidOut(POPUP);

		long visible = Reflow.measure().visibleWidth();
		assertTrue(Reflow.rightOf(POPUP) <= visible + 1,
				"the capped popup must stay inside the viewport, was " + Reflow.rectOf(POPUP)
				+ " visible=" + visible);
		assertTrue(Reflow.measure().conforms(),
				"and the caves must not spill onto the page: " + Reflow.measure());
		assertEquals("auto", Reflow.computed(POPUP, "overflow-x"),
				"the cap is only safe because the caves scroll inside - they must stay reachable");

		assertEquals(1, Reflow.removeDeclaration("max-width", POPUP),
				"expected za11y to cap " + POPUP);
		assertTrue(Reflow.rightOf(POPUP) > visible,
				"uncapped, the popup must extend past the right edge, was " + Reflow.rectOf(POPUP));
		assertTrue(Reflow.measure().overflow() > 0, "and must widen the page: " + Reflow.measure());

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
