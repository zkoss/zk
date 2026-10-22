/* B110_ZK_6173Test.java

        Purpose:

        Description:

        History:
                Tue Oct 13 10:32:04 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6173Test extends WebDriverTestCase {

	private void connectAndMount() {
		connect();
		assertNoJSError();
		waitResponse();
		assertSelectListsBothItems();
	}

	private void assertSelectListsBothItems() {
		assertTrue(jq("$lb").is("select"), "the listbox must render as a native <select>");
		JQuery options = jq("$lb").find("option");
		assertEquals(2, options.length(), "the select must hold one option per listitem");
		assertEquals("Net 30", options.eq(0).text());
		assertEquals("Net 60", options.eq(1).text());
	}

	private void assertNoErrorBox() {
		assertEquals("", jq(".z-error .messages").text(), "no client error expected");
	}

	@Test
	public void testListheadDoesNotBreakTheMount() {
		connectAndMount();
		assertEquals("Rendered after the listbox", jq("$after").text(),
				"a component after the listbox must render too");
		assertNoAnyError();
	}

	@Test
	public void testListheaderSetLabelFromServer() {
		connectAndMount();
		click(jq("$relabel"));
		waitResponse();
		assertNoErrorBox();
		assertEquals("Payment term", widget("$hdr").get("label"),
				"the new label must reach the listheader widget");
		assertSelectListsBothItems();
		assertNoAnyError();
	}

	@Test
	public void testListheaderSetResponsiveVisibleFromServer() {
		connectAndMount();
		click(jq("$hide"));
		waitResponse();
		assertNoErrorBox();
		assertFalse(widget("$hdr").is("responsiveVisible"),
				"responsiveVisible=false must reach the listheader widget");
		assertSelectListsBothItems();
		assertNoAnyError();
	}
}
