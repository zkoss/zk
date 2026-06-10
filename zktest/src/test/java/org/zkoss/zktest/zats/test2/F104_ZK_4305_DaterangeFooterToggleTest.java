/* F104_ZK_4305_DaterangeFooterToggleTest.java

		Purpose:

		Description:

		History:
				Thu Jun  4 15:12:54 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Regression for the H1 footer sub-node cache desync (ZK-4305).
 *
 * <p>The popup is bound once and only display-toggled across open/close, so
 * {@code Widget.$n()} keeps memoizing the Clear/Today sub-nodes for the popup's
 * lifetime. {@code _rebuildFooter} swaps those buttons with raw DOM on a runtime
 * {@code allowEmpty}/{@code showTodayLink} change; if it does not keep the
 * {@code $n()} cache in sync, a later rebuild reads a stale (detached) node and
 * skips the removal, leaving a button that should no longer exist visible. It
 * takes three toggles for Clear ({@code both -> none -> both -> none}) and two
 * for Today ({@code false -> true -> false}) to surface, so a single open
 * session is not enough — the popup must stay bound across the toggles.
 */
public class F104_ZK_4305_DaterangeFooterToggleTest extends WebDriverTestCase {

	/**
	 * Bind the popup, then close it (Cancel) so it stays bound but its overlay
	 * no longer intercepts clicks on the page toggle buttons. Closing keeps
	 * {@code _rangePopup} alive — exactly the across-open/close state H1 needs.
	 */
	private void bindThenClosePopup(String boxId) {
		click(jq(boxId + " .z-daterangebox-button"));
		waitResponse();
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
	}

	/** allowEmpty both -> none -> both -> none must leave no Clear button. */
	@Test
	public void testClearNotStaleAfterTriToggle() {
		connect("/test2/F104-ZK-4305-footer-toggle.zul");
		waitResponse();

		// allowEmpty=both renders Clear at bind time.
		bindThenClosePopup("$drToggle");
		assertEquals(1, jq("button.z-daterangebox-popup-clear").length(),
				"Pre-condition: Clear renders under the default allowEmpty=both");

		click(jq("$btnEmptyNone"));
		waitResponse();
		click(jq("$btnEmptyBoth"));
		waitResponse();
		click(jq("$btnEmptyNone"));
		waitResponse();

		assertEquals(0, jq("button.z-daterangebox-popup-clear").length(),
				"Clear button must be gone after both->none->both->none toggles");
	}

	/** showTodayLink false -> true -> false must leave no Today button. */
	@Test
	public void testTodayNotStaleAfterToggle() {
		connect("/test2/F104-ZK-4305-footer-toggle.zul");
		waitResponse();

		// showTodayLink=false at bind time -> no Today button.
		bindThenClosePopup("$drToggle");
		assertEquals(0, jq("button.z-daterangebox-popup-today").length(),
				"Pre-condition: Today is absent under the default showTodayLink=false");

		click(jq("$btnTodayOn"));
		waitResponse();
		assertEquals(1, jq("button.z-daterangebox-popup-today").length(),
				"Today must render after showTodayLink=true");

		click(jq("$btnTodayOff"));
		waitResponse();
		assertEquals(0, jq("button.z-daterangebox-popup-today").length(),
				"Today button must be gone after false->true->false toggles");
	}
}
