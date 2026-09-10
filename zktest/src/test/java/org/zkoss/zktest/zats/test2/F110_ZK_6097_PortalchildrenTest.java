/* F110_ZK_6097_PortalchildrenTest.java

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

public class F110_ZK_6097_PortalchildrenTest extends WebDriverTestCase {

	/** Reads an attribute, distinguishing "absent" from "empty". */
	private String attrOf(String selector, String attr) {
		return getEval("(function(){"
				+ " var e = jq('" + selector + "')[0];"
				+ " if (!e) return 'NO-NODE';"
				+ " var v = e.getAttribute('" + attr + "');"
				+ " return v === null ? 'ABSENT' : v;"
				+ "})()");
	}

	private boolean noA11y() {
		return !Boolean.valueOf(getEval("!!window.za11y"));
	}

	// ----- author-supplied ca:aria-labelledby -----

	@Test
	public void aria_labelledby_author_supplied_survives_bind() {
		connect();
		waitResponse();
		if (noA11y()) return;
		assertEquals("myLabel", attrOf("$pcCa", "aria-labelledby"),
				"author-supplied ca:aria-labelledby must survive the za11y augment");
	}

	@Test
	public void aria_labelledby_author_supplied_survives_set_title() {
		connect();
		waitResponse();
		if (noA11y()) return;
		click(jq("$btnRetitle"));
		waitResponse();
		assertEquals("myLabel", attrOf("$pcCa", "aria-labelledby"),
				"setTitle must not take over the author's accessible name");
	}

	@Test
	public void aria_labelledby_author_supplied_survives_clear_title() {
		// The else-branch of setTitle REMOVES the attribute, so clearing the
		// title would delete the author's name outright.
		connect();
		waitResponse();
		if (noA11y()) return;
		click(jq("$btnClearTitle"));
		waitResponse();
		assertEquals("myLabel", attrOf("$pcCa", "aria-labelledby"),
				"clearing the title must not delete the author's ca:aria-labelledby");
	}

	// ----- default wiring is untouched when the author supplied nothing -----

	@Test
	public void aria_labelledby_default_wiring_applies_without_ca() {
		connect();
		waitResponse();
		if (noA11y()) return;
		assertEquals(jq("$pcPlain").attr("id") + "-title",
				attrOf("$pcPlain", "aria-labelledby"),
				"a titled column with no ca:aria-* is still named by its title node");
	}

	@Test
	public void aria_labelledby_default_wiring_cleared_on_clear_title() {
		connect();
		waitResponse();
		if (noA11y()) return;
		click(jq("$btnClearTitle"));
		waitResponse();
		assertEquals("ABSENT", attrOf("$pcPlain", "aria-labelledby"),
				"the framework's own aria-labelledby is still removed with the title");
	}

	@Test
	public void aria_labelledby_default_wiring_kept_on_retitle() {
		connect();
		waitResponse();
		if (noA11y()) return;
		click(jq("$btnRetitle"));
		waitResponse();
		assertEquals(jq("$pcPlain").attr("id") + "-title",
				attrOf("$pcPlain", "aria-labelledby"),
				"renaming a column keeps it pointed at its title node");
	}
}
