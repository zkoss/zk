package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00657Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$listbox");
		JQuery intbox = jq("$intbox");
		assertEquals("0", listbox.toWidget().get("selectedIndex"));
		assertEquals("0", intbox.val());

		type(intbox, "1");
		waitResponse();
		assertEquals("1", listbox.toWidget().get("selectedIndex"));
		assertEquals("1", intbox.val());

		type(intbox, "2");
		waitResponse();
		assertEquals("2", listbox.toWidget().get("selectedIndex"));
		assertEquals("2", intbox.val());
	}
}
