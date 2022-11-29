package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3656Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery headerCm = jq(".z-listheader-checkable:eq(0)");
		click(headerCm);
		waitResponse();
		assertTrue(headerCm.hasClass("z-listheader-checked"), "The header checkbox status is wrong. (It should be checked)");
		int rowHeight = jq(".z-listitem").outerHeight();
		JQuery listBody = jq("@listbox .z-listbox-body");
		JQuery cm = jq(".z-listitem-checkbox > i");
		jq(listBody).toElement().set("scrollTop", Math.abs(rowHeight * 17)); // just shows item 20
		waitResponse();
		assertTrue(cm.eq(20).isVisible(), "The 20th checkbox status is wrong. (It should be checked)");
		assertEquals(2, jq(".z-listitem-selected").length());
		jq(listBody).toElement().set("scrollTop", Math.abs(rowHeight * 40)); // just shows item 44
		waitResponse();
		assertTrue(cm.eq(44).isVisible(), "The 44th checkbox status is wrong. (It should be checked)");
	}
}
