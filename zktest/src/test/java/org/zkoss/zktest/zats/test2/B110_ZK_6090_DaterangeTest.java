/* B110_ZK_6090_DaterangeTest.java

	Purpose:

	Description:

	History:
		Thu Aug 21 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * An open daterange popup puts itself in the onFloatUp watch list so that a click elsewhere closes
 * it, and only close() takes it out again. A popup unbound while it is open therefore keeps being
 * called, and reaches for a node that went away with the binding.
 *
 * <p>onFloatUp is not one of the visibility watches, so zWatch does not filter out the unbound
 * listener by itself.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_DaterangeTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		// control: a bound popup still closes on an outside float up
		openPopup("$drCtrl");
		assertEquals("true", getEval("zk6090PopupOpen('$drCtrl')"),
				"the control popup should be open");
		assertEquals("ok", getEval("zk6090FloatUp()"), "firing onFloatUp should not throw");
		assertEquals("false", getEval("zk6090PopupOpen('$drCtrl')"),
				"an outside float up should close a bound popup");

		// the popup is unbound while still open, which is what a container doing
		// render-on-demand does to its children
		openPopup("$dr");
		assertEquals("true", getEval("zk6090UnbindPopup('$dr')"),
				"the first box should own a popup");
		assertEquals("false", getEval("String(!!window.zk6090Popup.desktop)"),
				"the popup should have been unbound");
		// ZK-6090: unbind_ left the popup in the watch list, so onFloatUp reached close() and
		// its this.$n_() on a widget whose node was already gone
		assertEquals("ok", getEval("zk6090FloatUp()"),
				"an unbound popup should no longer be called on onFloatUp");
		assertNoAnyError();
	}

	private void openPopup(String boxId) {
		click(jq(boxId + " .z-daterangebox-button"));
		waitResponse();
	}
}
