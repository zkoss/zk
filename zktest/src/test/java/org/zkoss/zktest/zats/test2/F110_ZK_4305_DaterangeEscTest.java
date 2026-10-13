/* F110_ZK_4305_DaterangeEscTest.java

		Purpose:

		Description:

		History:
				Wed Sep  2 14:18:29 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Regression for the ESC escalation (ZK-4305).
 *
 * <p>The framework binds one {@code keydown} listener on {@code document} at
 * boot; when it does not see the event stopped it calls
 * {@code zul.Widget#afterKeyDown_}, which maps ESC to {@code onCancel} and walks
 * up to the first listening ancestor — the enclosing modal window. Nothing in
 * the popup's path stops that: {@code zul.db.Calendar#doKeyDown_} never chains
 * to {@code super}, and the calendar is where {@code open} puts real focus. So a
 * single ESC used to dismiss the popup <em>and</em> cancel the window, discarding
 * the form the user was filling in.
 *
 * <p>The box therefore consumes the ESC in its own {@code doKeyDown_} while the
 * popup is open, as {@code zul.db.Datebox} does, and stops only the widget
 * propagation. The control below is the other half of the claim: with the popup
 * closed the same key must still reach the window, so the fix cannot have been
 * "swallow ESC".
 */
public class F110_ZK_4305_DaterangeEscTest extends WebDriverTestCase {

	private static final String STATUS_OPEN = "not-cancelled";
	private static final String STATUS_CANCELLED = "cancelled";

	private static final String KEY_NONE = "no-keydown";
	private static final String KEY_ARROW_LEFT = "keydown-37";

	/** Opens the popup with the trigger button; focus lands in the calendar. */
	private void openPopup() {
		openPopup("drb");
	}

	private void openPopup(String boxId) {
		click(jq("$" + boxId + " .z-daterangebox-button"));
		waitResponse();
		assertEquals(1, jq(".z-daterangebox-popup").length(),
				"Pre-condition: the trigger button opens the popup");
		assertTrue(focusInPopup(), "Pre-condition: open() moves focus into the calendar");
	}

	private void pressEscape() {
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
	}

	/** close() only hides the node, so display is what "closed" means here. */
	private String popupDisplay() {
		return getEval("document.querySelector('.z-daterangebox-popup').style.display");
	}

	private void assertPopupClosed() {
		assertEquals("none", popupDisplay(), "ESC must dismiss the popup");
	}

	/** Records {@code defaultPrevented} of the next ESC that bubbles to {@code target}
	 *  ("document": after zk.mount's own listener; "window": past document); else "not-seen". */
	private void installProbe(String target) {
		// eval() wraps its argument in parentheses, so it takes one expression:
		// several statements have to go inside an IIFE or it is a syntax error.
		eval("function () {"
				+ "window.__zk4305Prevented = 'not-seen';"
				+ "window.__zk4305Probe = function (e) {"
				+ " if (e.key === 'Escape') window.__zk4305Prevented = String(e.defaultPrevented); };"
				+ target + ".addEventListener('keydown', window.__zk4305Probe);"
				+ "}()");
	}

	private String probe() {
		return getEval("String(window.__zk4305Prevented)");
	}

	private boolean focusInPopup() {
		return Boolean.parseBoolean(getEval("String(document.querySelector('.z-daterangebox-popup')"
				+ ".contains(document.activeElement))"));
	}

	/** Discriminator: ESC from the calendar dismisses the popup only. */
	@Test
	public void testEscInPopupDoesNotCancelWindow() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		pressEscape();

		assertPopupClosed();
		// The dismissal round-trips onOpen(false), so the label has had a server
		// response to change in — its staying put is a real negative, not a race.
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"ESC consumed by the popup must not escalate to the window's onCancel");
	}

	/**
	 * Discriminator, input-focused path: the ESC targets the box itself, not the
	 * calendar. Clicking the begin input while the popup is open keeps it open —
	 * the box is not "outside" the popup for the focus-out handler.
	 */
	@Test
	public void testEscFromBeginInputDoesNotCancelWindow() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertNotEquals("none", popupDisplay(),
				"Pre-condition: focusing the begin input leaves the popup open");
		pressEscape();

		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"ESC from the begin input must not escalate while the popup is open");
	}

	/** Discriminator, footer path: a plain button in the popup resolves to the popup widget. */
	@Test
	public void testEscFromFooterButtonDoesNotCancelWindow() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		focus(jq(".z-daterangebox-popup-cancel"));
		assertEquals("true", getEval("String(document.activeElement"
						+ " === document.querySelector('.z-daterangebox-popup-cancel'))"),
				"Pre-condition: the Cancel button holds focus");
		assertNotEquals("none", popupDisplay(),
				"Pre-condition: focusing the Cancel button leaves the popup open");
		pressEscape();

		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"ESC from the footer must not escalate while the popup is open");
	}

	/** Discriminator, showTime path: the ESC starts in a Timebox child of the popup. */
	@Test
	public void testEscFromTimeboxDoesNotCancelWindow() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup("drbTime");
		click(jq(".z-daterangebox-popup .z-timebox-input:first"));
		assertEquals("true", getEval("String(document.activeElement"
						+ " === document.querySelector('.z-daterangebox-popup .z-timebox-input'))"),
				"Pre-condition: the begin Timebox holds focus");
		assertNotEquals("none", popupDisplay(),
				"Pre-condition: focusing a Timebox leaves the popup open");
		pressEscape();

		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"ESC from a Timebox must not escalate while the popup is open");
	}

	/**
	 * ESC reaches the box only through the focused widget, so mouse navigation
	 * inside the calendar must leave focus within the popup.
	 */
	@Test
	public void testEscAfterMouseNavigationClosesPopup() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		// Linked panels hide the inner `>` with visibility:hidden; only the last one is clickable.
		click(jq(".z-daterangebox-popup .z-calendar-right:last"));
		waitResponse(true);
		// The title click below re-focuses the calendar itself, so check the arrow alone here.
		assertTrue(focusInPopup(), "Pre-condition: the month arrow must leave focus in the popup");
		click(jq(".z-daterangebox-popup .z-calendar-title:first"));
		waitResponse(true);
		assertNotEquals("none", popupDisplay(),
				"Pre-condition: navigating inside the calendar leaves the popup open");
		pressEscape();

		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"ESC after mouse navigation must not escalate while the popup is open");
	}

	/** A read-only time field (tablet UI makes every Timebox one) returns before chaining up. */
	@Test
	public void testEscFromReadonlyTimeboxClosesPopup() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup("drbTime");
		eval("jq('.z-daterangebox-popup .z-timebox-input')[0].readOnly = true");
		click(jq(".z-daterangebox-popup .z-timebox-input:first"));
		assertEquals("true", getEval("String(document.activeElement"
						+ " === document.querySelector('.z-daterangebox-popup .z-timebox-input'))"),
				"Pre-condition: the read-only begin Timebox holds focus");
		pressEscape();

		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"ESC from a read-only Timebox must not escalate while the popup is open");
	}

	/**
	 * Pins that the ESC the popup consumes keeps going past {@code document}: the
	 * box stops only the widget chain, so a {@code window} listener still sees the
	 * key with its default intact (the close's onOpen leaves after the AU delay, so
	 * the ESC eat does not apply).
	 */
	@Test
	public void testEscInPopupStillReachesWindowListeners() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		installProbe("window");

		pressEscape();

		assertPopupClosed();
		assertEquals("false", probe(),
				"The ESC that closes the popup must still reach window listeners, default intact");
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"Reaching window must not mean reaching the window's onCancel");
	}

	/**
	 * Discriminator: the framework's ESC eat (Bug 1927788) still applies to the
	 * ESC the popup consumes.
	 *
	 * <p>{@code zk.mount}'s boot-time handler eats ESC while
	 * {@code zk._noESC > 0} (a lazy package is loading) or an AU request is in
	 * flight. The popup's ESC must keep passing through that handler rather than
	 * re-implementing the eat, so the probe reads {@code defaultPrevented} after
	 * it, in bubble phase.
	 */
	@Test
	public void testEscWhileEscDisabledStillPreventsDefault() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		installProbe("document");
		// zk.disableESC() cannot arm the guard (_noESC is already negative on a loaded
		// page), so set the counter directly: this covers the eat, not that defect.
		eval("zk._noESC = 1");

		pressEscape();

		assertEquals("true", probe(),
				"With ESC disabled the framework's eat (preventDefault) must still apply");
		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"The eat must not come at the cost of letting ESC reach the window");
	}

	/**
	 * Pins that a widget onKeyDown listener on the box does not see the ESC that
	 * closed the popup: the box consumes it before its own listeners fire, as
	 * {@code zul.db.Datebox#escPressed_} does.
	 * ({@code ctrlKeys="#esc"} is not an alternative probe: the client parser
	 * knows no {@code esc} token, and ESC is routed only as onCancel.)
	 */
	@Test
	public void testEscInPopupIsHiddenFromWidgetKeyDownListeners() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();
		assertEquals(KEY_NONE, jq("$keyStatus").text(),
				"Pre-condition: no key has reached the box's onKeyDown listener yet");

		// Wiring check: a plain key DOES reach the listener, so a miss after ESC
		// is about the box consuming it, not a dead probe.
		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();
		assertEquals(KEY_ARROW_LEFT, jq("$keyStatus").text(),
				"Pre-condition: the widget onKeyDown listener is live on the closed box");

		openPopup();
		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertNotEquals("none", popupDisplay(),
				"Pre-condition: focusing the begin input leaves the popup open");

		pressEscape();

		assertPopupClosed();
		assertEquals(KEY_ARROW_LEFT, jq("$keyStatus").text(),
				"While the popup is open the box consumes the ESC before its own "
						+ "onKeyDown listeners fire");
	}

	/** Control: with no popup open, ESC must still cancel the window. */
	@Test
	public void testEscWithoutPopupCancelsWindow() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"Pre-condition: the window has not been cancelled yet");

		pressEscape();

		assertEquals(STATUS_CANCELLED, jq("$status").text(),
				"ESC must still reach the window's onCancel when no popup is open");
	}
}
