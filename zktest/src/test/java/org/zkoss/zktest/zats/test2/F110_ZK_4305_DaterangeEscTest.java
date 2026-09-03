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
 * <p>The popup's dismissal listener therefore runs in capture phase and stops
 * the ESC it consumes. The control below is the other half of the claim: with
 * the popup closed the same key must still reach the window, so the fix cannot
 * have been "swallow ESC".
 */
public class F110_ZK_4305_DaterangeEscTest extends WebDriverTestCase {

	private static final String STATUS_OPEN = "not-cancelled";
	private static final String STATUS_CANCELLED = "cancelled";

	/** Opens the popup with the trigger button; focus lands in the calendar. */
	private void openPopup() {
		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertEquals(1, jq(".z-daterangebox-popup").length(),
				"Pre-condition: the trigger button opens the popup");
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
	 * Discriminator, input-focused path: {@code Daterangebox#doKeyDown_} forwards
	 * everything but Alt+Up/Down to super, so ESC escalated from the input too.
	 * Clicking the begin input while the popup is open keeps it open — the box is
	 * not "outside" the popup for the focus-out handler.
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

	/**
	 * Discriminator: stopping the ESC in capture phase also cancels the
	 * framework's own eat.
	 *
	 * <p>{@code zk.mount}'s boot-time handler is bound with jQuery, i.e. bubble
	 * phase only, and its last statement eats ESC while {@code zk._noESC > 0}
	 * (a lazy package is loading) or an AU request is in flight — Bug 1927788.
	 * The popup's capture listener stops propagation before that handler ever
	 * runs, and {@code canActivate({checkOnly: true})} deliberately skips the
	 * busy check, so the eat was lost in exactly the window it exists for. The
	 * popup must therefore re-apply it with {@code preventDefault()}.
	 *
	 * <p>The probe is registered on {@code document} in capture phase
	 * <em>after</em> the popup opened: same node, same phase, later
	 * registration, so it runs second and observes what the popup's own
	 * listener did. {@code stopPropagation} (not the immediate variant) does not
	 * silence it.
	 */
	@Test
	public void testEscWhileEscDisabledStillPreventsDefault() {
		connect("/test2/F110-ZK-4305-esc.zul");
		waitResponse();

		openPopup();
		// eval() wraps its argument in parentheses, so it takes one expression:
		// several statements have to go inside an IIFE or it is a syntax error.
		// zk.disableESC() cannot arm the guard: the package loader calls it once per
		// load burst but enableESC() once per doEnd(), so zk._noESC is already
		// negative on a loaded page and `zk._noESC > 0` never holds. Set the counter
		// directly so this covers the popup's re-apply, not that separate defect.
		eval("function () {"
				+ "window.__zk4305Prevented = 'not-seen';"
				+ "window.__zk4305NoESC = zk._noESC;"
				+ "window.__zk4305Probe = function (e) {"
				+ " if (e.key === 'Escape') window.__zk4305Prevented = String(e.defaultPrevented); };"
				+ "document.addEventListener('keydown', window.__zk4305Probe, true);"
				+ "zk._noESC = 1;"
				+ "}()");

		pressEscape();

		assertEquals("true", getEval("String(window.__zk4305Prevented)"),
				"With ESC disabled the popup must re-apply the framework's eat (preventDefault)");
		assertPopupClosed();
		assertEquals(STATUS_OPEN, jq("$status").text(),
				"The eat must not come at the cost of letting ESC reach the window");

		eval("function () {"
				+ "zk._noESC = window.__zk4305NoESC;"
				+ "document.removeEventListener('keydown', window.__zk4305Probe, true);"
				+ "}()");
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
