package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00843SelectboxSelectedItemTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery selectbox = jq("$selectbox");
		JQuery listbox = jq("$listbox");
		JQuery combobox = jq("$combobox");
		JQuery l1 = jq("$l1");
		JQuery items = listbox.find("@listitem");

		click(items.eq(1));
		waitResponse();
		assertEquals("1", listbox.toWidget().get("selectedIndex"));
		assertEquals("1", selectbox.toWidget().get("selectedIndex"));
		assertEquals("B", combobox.toWidget().get("text"));
		assertEquals("B", l1.text());

		click(items.eq(2));
		waitResponse();
		assertEquals("2", listbox.toWidget().get("selectedIndex"));
		assertEquals("2", selectbox.toWidget().get("selectedIndex"));
		assertEquals("C", combobox.toWidget().get("text"));
		assertEquals("C", l1.text());
	}
}
