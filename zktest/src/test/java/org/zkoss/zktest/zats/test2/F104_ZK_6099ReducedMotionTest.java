/* F104_ZK_6099ReducedMotionTest.java

		Purpose:

		Description:

		History:
				Tue Jun 16 09:52:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/** Accessibility (WCAG 2.3.3 / Success Criterion 2.2.2): under
 *  {@code prefers-reduced-motion: reduce} the skeleton pulse animation must be
 *  disabled. skeleton.less carries an {@code @media (prefers-reduced-motion:
 *  reduce)} block that sets {@code animation: none} on both {@code .z-skeleton-active}
 *  (the in-place ::after path) and {@code .z-skeleton-overlay} (the replaced-root
 *  overlay). This test forces the media feature with a Chrome flag — mirroring the
 *  {@code getWebDriverOptions()} override pattern used by other capability-specific
 *  tests (e.g. B102_ZK_5615_DE_Test's {@code --lang}) — and reuses the shared
 *  F104-ZK-6099.zul page, asserting the computed animation is disabled on both paths.
 */
public class F104_ZK_6099ReducedMotionTest extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions().addArguments("--force-prefers-reduced-motion");
	}

	@Test
	public void test() {
		connect("/test2/F104-ZK-6099.zul");
		waitResponse();

		// In-place ::after path: the grid root carries z-skeleton-active.
		assertEquals("none",
				getEval("window.getComputedStyle(jq('$g1')[0]).animationName"),
				"z-skeleton-active must disable the pulse animation under prefers-reduced-motion");

		// Replaced-root overlay path: the <img>'s separate overlay div.
		assertEquals("none",
				getEval("window.getComputedStyle(jq('$i1').parent().find('.z-skeleton-overlay')[0]).animationName"),
				"z-skeleton-overlay must disable the pulse animation under prefers-reduced-motion");
	}
}
