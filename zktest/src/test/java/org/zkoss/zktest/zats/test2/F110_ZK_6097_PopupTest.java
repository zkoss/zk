/* F110_ZK_6097_PopupTest.java

        Purpose:

        Description:

        History:
                Sun Aug 30 18:10:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6097_PopupTest extends WebDriverTestCase {

	/** Reads an attribute off a JS node expression, telling "absent" from "empty". */
	private String attrOf(String nodeExpr, String attr) {
		return getEval("(function(){"
				+ " var e = " + nodeExpr + ";"
				+ " if (!e) return 'NO-NODE';"
				+ " var v = e.getAttribute('" + attr + "');"
				+ " return v === null ? 'ABSENT' : v;"
				+ "})()");
	}

	/** A closed popup is not in the DOM, so it can only be reached as a widget. */
	private void openPopup(String popup, String anchor) {
		getEval("(function(){"
				+ " zk.Widget.$('" + popup + "').open(zk.Widget.$('" + anchor + "'));"
				+ " return '';})()");
		waitResponse();
	}

	private void closePopup(String popup) {
		getEval("(function(){ zk.Widget.$('" + popup + "').close(); return '';})()");
		waitResponse();
	}

	private String uuidOf(String selector) {
		return getEval("zk.Widget.$('" + selector + "').uuid");
	}

	private boolean noA11y() {
		return !Boolean.valueOf(getEval("!!window.za11y"));
	}

	// ----- author-supplied ca:aria-* on the popup's reference node -----

	@Test
	public void aria_describedby_author_supplied_survives_open() {
		connect();
		waitResponse();
		if (noA11y()) return;
		openPopup("$popA", "$btnCa");
		assertEquals("myHint", attrOf("jq('$btnCa')[0]", "aria-describedby"),
				"author-supplied ca:aria-describedby must survive opening the popup");
	}

	@Test
	public void aria_describedby_author_supplied_survives_close() {
		// close() REMOVES the attribute, so an authored value skipped by open()
		// must be skipped here too or it is deleted for good.
		connect();
		waitResponse();
		if (noA11y()) return;
		openPopup("$popA", "$btnCa");
		closePopup("$popA");
		assertEquals("myHint", attrOf("jq('$btnCa')[0]", "aria-describedby"),
				"closing the popup must not delete the author's ca:aria-describedby");
	}

	@Test
	public void aria_owns_author_supplied_survives_open_and_close() {
		connect();
		waitResponse();
		if (noA11y()) return;
		openPopup("$popA", "$btnCa");
		assertEquals("myOwned", attrOf("jq('$btnCa')[0]", "aria-owns"),
				"author-supplied ca:aria-owns must survive opening the popup");
		closePopup("$popA");
		assertEquals("myOwned", attrOf("jq('$btnCa')[0]", "aria-owns"),
				"closing the popup must not delete the author's ca:aria-owns");
	}

	@Test
	public void aria_describedby_author_supplied_survives_a_real_click() {
		// The documented path: popup="popA" opens on click through
		// zul.Widget.doClick_, not only through a scripted open().
		connect();
		waitResponse();
		if (noA11y()) return;
		click(jq("$btnCa"));
		waitResponse();
		assertEquals("myHint", attrOf("jq('$btnCa')[0]", "aria-describedby"),
				"clicking the anchor must not take over its ca:aria-describedby");
	}

	// ----- default wiring is untouched when the author supplied nothing -----

	@Test
	public void aria_describedby_default_wiring_applies_without_ca() {
		connect();
		waitResponse();
		if (noA11y()) return;
		String popUuid = uuidOf("$popB");
		openPopup("$popB", "$btnPlain");
		assertEquals(popUuid, attrOf("jq('$btnPlain')[0]", "aria-describedby"),
				"an anchor with no ca:aria-describedby still points at the popup");
		assertEquals(popUuid, attrOf("jq('$btnPlain')[0]", "aria-owns"),
				"an anchor with no ca:aria-owns still owns the popup");
	}

	@Test
	public void aria_describedby_default_wiring_cleared_on_close() {
		connect();
		waitResponse();
		if (noA11y()) return;
		openPopup("$popB", "$btnPlain");
		closePopup("$popB");
		assertEquals("ABSENT", attrOf("jq('$btnPlain')[0]", "aria-describedby"),
				"the framework's own aria-describedby is still removed on close");
		assertEquals("ABSENT", attrOf("jq('$btnPlain')[0]", "aria-owns"),
				"the framework's own aria-owns is still removed on close");
	}

	// ----- anchor whose a11y node is the '-real' subnode -----

	private static final String CB_REAL = "zk.Widget.$('$cbCa').$n('real')";

	@Test
	public void aria_describedby_moved_to_real_node_is_still_protected() {
		// za11y moves ca:aria-describedby onto the '-real' input — the very node
		// _getRefNode picks, so the root-only check would miss the collision.
		connect();
		waitResponse();
		if (noA11y()) return;
		assertEquals("myHint", attrOf(CB_REAL, "aria-describedby"),
				"baseline: za11y relocates the author's value onto '-real'");
		openPopup("$popC", "$cbCa");
		assertEquals("myHint", attrOf(CB_REAL, "aria-describedby"),
				"opening the popup must not take over the relocated author value");
		closePopup("$popC");
		assertEquals("myHint", attrOf(CB_REAL, "aria-describedby"),
				"closing the popup must not delete the relocated author value");
	}
}
