/* F110_ZK_4305_DaterangeRerenderTest.java

		Purpose:

		Description:

		History:
				Wed Sep  2 09:36:24 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Regression for the client-rerender crash (ZK-4305).
 *
 * <p>{@code _openPopup} makes the {@code DaterangePopup} a real widget child of
 * the box and moves its DOM under {@code <body>}; {@code unbind_} removes that
 * node but never releases the widget. A client-side rerender therefore unbinds,
 * redraws markup that carries no popup, and then rebinds the surviving popup
 * child — whose {@code bind_} does {@code ('cancel')} and throws
 * "Node with cancel is not found!". The throw happens inside the box's own
 * {@code super.bind_}, its first statement, so the box never attaches its input
 * and button listeners and the popup can never be opened again.
 *
 * <p>The page rerenders via {@code setZclass("z-daterangebox")} — already the
 * default zclass, so the emitted markup is identical and the test isolates the
 * rerender from any CSS side effect. The same client path is reached by
 * {@code setMold}, {@code setCssflex} and any ancestor rerender.
 */
public class F110_ZK_4305_DaterangeRerenderTest extends WebDriverTestCase {

	/** Opens the popup with the trigger button and closes it again with Cancel. */
	private void openThenClosePopup() {
		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertEquals(1, jq(".z-daterangebox-popup").length(),
				"Pre-condition: the trigger button opens the popup");
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
	}

	/** After the popup has been created, a rerender must not break the box. */
	@Test
	public void testPopupReopensAfterRerender() {
		connect("/test2/F110-ZK-4305-rerender.zul");
		waitResponse();

		openThenClosePopup();

		click(jq("$btnRerender"));
		waitResponse();
		// unbind_ takes the body-mounted popup node with it, so its absence is
		// proof the rerender actually ran — true both with and without the fix.
		assertEquals(0, jq(".z-daterangebox-popup").length(),
				"The rerender must have unbound the previous popup DOM");

		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertEquals(1, jq(".z-daterangebox-popup").length(),
				"The trigger button must still open the popup after a rerender");
		// Reported last so the functional breakage above names the failure first;
		// the crash itself surfaces here as an uncaught throw during bind.
		assertNoJSError();
	}

	/** Control: a rerender before any popup exists is harmless either way. */
	@Test
	public void testPopupOpensAfterRerenderWithoutPriorPopup() {
		connect("/test2/F110-ZK-4305-rerender.zul");
		waitResponse();

		click(jq("$btnRerender"));
		waitResponse();
		assertNoJSError();

		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertEquals(1, jq(".z-daterangebox-popup").length(),
				"The trigger button must open the popup after a rerender");
	}
}
