/* F110_ZK_6084_ImageReflowTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 20:00:00 CST 2026, Created by peggypeng

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
 * <p>CE ships no {@code .z-image} rule, so an image renders at its intrinsic width and pushes the
 * page sideways. za11y caps it in {@code zul/less/_wgt-a11y.less}; stripping the cap at run time
 * reproduces CE, so the passing half cannot pass vacuously.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_ImageReflowTest extends WebDriverTestCase {
	private static final String IMAGES = ".z-image";

	@AfterEach
	public void restoreViewport() {
		Reflow.clearViewport(getWebDriver());
	}

	@Test
	public void imagesShrinkToTheViewport() {
		connect();
		waitResponse();
		narrowTo(Reflow.WIDTH, Reflow.HEIGHT);

		assertEquals(0, Reflow.outsideViewport(IMAGES),
				"both capped images must stay inside the viewport, first is " + Reflow.rectOf(IMAGES));

		assertEquals(1, Reflow.removeDeclaration("max-width", IMAGES),
				"expected za11y to cap " + IMAGES);
		assertEquals(2, Reflow.outsideViewport(IMAGES),
				"uncapped, both images must render at intrinsic width and overflow, first is "
				+ Reflow.rectOf(IMAGES));
		assertTrue(Reflow.measure().overflow() > 0, "and widen the page: " + Reflow.measure());
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
