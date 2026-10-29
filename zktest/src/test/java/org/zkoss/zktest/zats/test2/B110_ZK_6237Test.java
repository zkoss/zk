/* B110_ZK_6237Test.java

	Purpose:

	Description:

	History:
		Thu Oct 29 14:16:32 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6237Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertValueFits("cb1");
		assertValueFits("cb2");
	}

	@Test
	public void testInplaceWhileEditing() {
		connect();
		click(jq("$cb3 input"));
		waitResponse();
		assertFalse(jq("$cb3").hasClass("z-combobox-inplace"), "cb3 should leave the inplace look while edited");
		assertValueFits("cb3");
	}

	@Test
	public void testInplaceKeepsWidthAfterEditing() {
		connect();
		int width = jq("$cb3").outerWidth();
		click(jq("$cb3 input"));
		waitResponse();
		click(jq("@label"));
		waitResponse();
		sleep(500); // the inplace look comes back after InputWidget's 150ms timer
		assertTrue(jq("$cb3").hasClass("z-combobox-inplace"), "cb3 should be back in the inplace look");
		assertEquals(width, jq("$cb3").outerWidth(), 1, "cb3 should keep the hflex=\"min\" width after editing");
	}

	@Test
	public void testButtonVisibleToggled() {
		connect();
		int width = jq("$cb4").outerWidth();
		click(jq("$toggle"));
		waitResponse();
		assertTrue(jq(widget("$cb4").$n("btn")).isVisible(), "cb4 should show its button");
		assertValueFits("cb4");
		click(jq("$toggle"));
		waitResponse();
		assertEquals(width, jq("$cb4").outerWidth(), 1, "cb4 should shrink back once the button is hidden again");
	}

	private void assertValueFits(String id) {
		assertTrue(Boolean.parseBoolean(getEval("(function () { var n = zk.Widget.$(jq('$" + id + "')).getInputNode();"
				+ " return n.scrollWidth <= n.clientWidth; })()")), id + ": the dropdown button covers the end of the value");
	}
}
