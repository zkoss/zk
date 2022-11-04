package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01528NPEInPagingMoldTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$listbox");
		JQuery items = listbox.find("@listitem");
		click(items.eq(9));
		waitResponse();

		assertEquals("Item 9", jq("$tb").val());

		click(jq("$delete"));
		waitResponse();
		String v = jq("$tb").val();
		assertTrue(v == null || v.equals(""));

		items = listbox.find("@listitem");
		assertEquals(9, items.length());
	}
}
