/* F110_ZK_6084_MessageboxReflowTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 10:00:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.Reflow;

/**
 * WCAG 1.4.10 Reflow, the Messagebox half of ZK-6084 - https://www.w3.org/WAI/WCAG21/Understanding/reflow
 *
 * <p>Messagebox fails two independent ways, both fixed in za11y: a fixed {@code width: 480px}
 * that cannot fit 320px ({@code zul/less/_wnd-a11y.less}), and a {@code highlighted} window
 * keeping the {@code left} it computed for the old viewport, because CE re-centres {@code modal}
 * only ({@code zul/wnd-a11y.ts}).
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_MessageboxReflowTest extends WebDriverTestCase {
	private static final String BOX = ".z-messagebox-window";
	/** The icon + message row: {@code overflow:auto} + {@code white-space:nowrap}, so it can hide text. */
	private static final String MESSAGE_ROW = ".z-messagebox-viewport";
	/** {@code --zk-messagebox-window-width} on the default profile; the compact one is 360. */
	private static final long FIXED_WIDTH = 480;

	@AfterEach
	public void restoreViewport() {
		Reflow.clearViewport(getWebDriver());
	}

	/** Strips the caps at run time to reproduce CE's CSS, so the passing half cannot pass vacuously. */
	@Test
	public void theCapsAreWhatMakeTheDialogFit() {
		connect();
		waitResponse();
		narrowTo(Reflow.WIDTH, Reflow.HEIGHT);
		openMessagebox();

		long visible = Reflow.measure().visibleWidth();
		assertTrue(Reflow.widthOf(BOX) <= visible + 1, "the capped dialog must shrink to the "
				+ "viewport, was " + Reflow.rectOf(BOX) + " visible=" + visible);
		assertEquals(0, Reflow.outsideViewport(BOX),
				"and must sit inside it, " + Reflow.rectOf(BOX));
		assertEquals(0, Reflow.hiddenContentOf(MESSAGE_ROW), "the message must wrap, not hide "
				+ "behind the row's own horizontal scroll - unlike a popup, prose has to reflow");
		assertEquals(0, Reflow.outsideViewport(BOX + " .z-messagebox-button, " + BOX + " .z-window-close"),
				"every dialog button must be reachable without scrolling sideways");

		assertEquals(2, Reflow.removeDeclaration("max-width", BOX, ".z-messagebox"),
				"expected za11y to cap both " + BOX + " and .z-messagebox");

		assertEquals(FIXED_WIDTH, Reflow.widthOf(BOX),
				"uncapped, the dialog returns to its fixed desktop width");
		assertEquals(1, Reflow.outsideViewport(BOX),
				"and must extend past the right edge, " + Reflow.rectOf(BOX));
		assertTrue(Reflow.measure().overflow() > 0, "and must widen the page: " + Reflow.measure());

		assertEquals(2, Reflow.restoreDeclarations(), "the caps must go back on");
		assertTrue(Reflow.widthOf(BOX) <= visible + 1, "restoring the caps must shrink it again");
	}

	/** 400% zoom on an already-open dialog: {@code left} not moving is itself the failure. */
	@Test
	public void narrowingAnOpenDialogRecentresIt() {
		connect();
		waitResponse();
		narrowTo(Reflow.REFERENCE_WIDTH, Reflow.REFERENCE_HEIGHT);
		openMessagebox();

		long leftAtReference = left();
		assertTrue(leftAtReference > Reflow.WIDTH,
				"precondition: at 1280px the centred dialog must start beyond x=320, so a stale "
				+ "left would leave it entirely off-screen; was " + leftAtReference);

		narrowTo(Reflow.WIDTH, Reflow.HEIGHT);

		assertNotEquals(leftAtReference, left(),
				"a highlighted dialog must re-centre when the viewport narrows");
		assertEquals(0, Reflow.outsideViewport(BOX),
				"and must end up inside the viewport, " + Reflow.rectOf(BOX));
	}

	// --- driving -----------------------------------------------------------------------------

	private void narrowTo(int width, int height) {
		WebDriver driver = getWebDriver();
		Assumptions.assumeTrue(Reflow.isSupported(driver),
				"a true " + width + "px CSS viewport needs a CDP device-metrics override");
		assertEquals(width, Reflow.setViewport(driver, width, height),
				"the CSS viewport did not reach " + width + "px");
		waitResponse(true);
	}

	private void openMessagebox() {
		click(jq("$show"));
		// the dialog is positioned after its open animation, not when it is appended
		waitResponse(true);
	}

	/** The absolute {@code left} ZK computed for the dialog, rounded. */
	private long left() {
		return Long.parseLong(getEval(
				"Math.round(parseFloat(document.querySelector('" + BOX + "').style.left))"));
	}
}
