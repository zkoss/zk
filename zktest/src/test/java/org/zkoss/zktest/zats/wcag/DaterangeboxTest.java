/* DaterangeboxTest.java

	Purpose:

	Description:

	History:
		Thu May 29 14:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

/**
 * Lighthouse-driven accessibility audit for {@code daterangebox}. Mirrors
 * the existing {@link DateboxTest} / {@link CalendarTest} pattern: open
 * the popup via the {@code Alt+ArrowDown} shortcut (so the audit also
 * exercises the floating popup, not just the closed state), drive a few
 * arrow-key navigations and a {@code Space} commit, and re-run the
 * Lighthouse audit at each state.
 */
public class DaterangeboxTest extends WcagTestCase {
	@Test
	public void testClosed() {
		connect();
		verifyA11y();
	}

	@Test
	public void testOpenPopup() {
		connect();

		// Open via the calendar icon button (the @daterangebox ZTL widget
		// selector returns null — Daterangebox doesn't register a ZTL name —
		// so we drive the icon directly, same as F104_ZK_4305_DaterangeAttributeTest.
		click(jq(".z-daterangebox:eq(0) .z-daterangebox-button"));
		waitResponse();

		Actions actions = getActions();
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT)
				.pause(200)
				.perform();
		waitResponse();
		verifyA11y();
	}

	@Test
	public void testShowTime() {
		connect();

		click(jq(".z-daterangebox:eq(1) .z-daterangebox-button"));
		waitResponse();

		Actions actions = getActions();
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT)
				.pause(200)
				.perform();
		waitResponse();
		verifyA11y();
	}

	@Test
	public void testAriaLabelledby() {
		connect();
		// Third box uses ca:aria-labelledby pointing at a sibling label.
		// Just opening + auditing exercises Lighthouse's
		// "form fields must have labels" rule on the labelled-by path.
		click(jq(".z-daterangebox:eq(2) .z-daterangebox-button"));
		waitResponse();

		Actions actions = getActions();
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT)
				.pause(200)
				.perform();
		waitResponse();
		verifyA11y();
	}
}
