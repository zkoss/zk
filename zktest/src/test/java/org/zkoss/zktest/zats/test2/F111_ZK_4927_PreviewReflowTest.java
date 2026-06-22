/* F111_ZK_4927_PreviewReflowTest.java

	Purpose:

	Description:

	History:
		Sat Oct 24 10:00:00 CST 2026, Created by peakerlee

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
 */
@Tag("WcagTestOnly")
public class F111_ZK_4927_PreviewReflowTest extends WebDriverTestCase {
	@AfterEach
	public void restoreViewport() {
		Reflow.clearViewport(getWebDriver());
	}

	@Test
	public void previewContentFitsTheViewport() {
		connect();
		waitResponse();
		WebDriver driver = getWebDriver();
		Assumptions.assumeTrue(Reflow.isSupported(driver), "a true 320px CSS viewport needs a CDP device-metrics override");
		assertEquals(Reflow.WIDTH, Reflow.setViewport(driver, Reflow.WIDTH, Reflow.HEIGHT), "the CSS viewport did not reach 320px");
		waitResponse(true);
		Reflow.awaitLaidOut(".z-mdeditor-preview-pane img");

		assertEquals(0, Reflow.outsideViewport(".z-mdeditor-preview-pane img"),
				"the image must shrink to the pane, it is " + Reflow.rectOf(".z-mdeditor-preview-pane img"));
		assertEquals(0, Reflow.hiddenContentOf(".z-mdeditor-preview-pane"), "the long word must wrap, not hide behind the pane's scrollbar");
		Reflow reflow = Reflow.measure();
		assertTrue(reflow.conforms(), reflow.toString());
	}
}
