package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01085NPEReferenceBindingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox1 = jq("$listbox1");
		JQuery listbox2 = jq("$listbox2");
		JQuery listbox3 = jq("$listbox3");
		JQuery listbox4 = jq("$listbox4");
		JQuery listbox5 = jq("$listbox5");
		JQuery listbox6 = jq("$listbox6");
		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");

		assertEquals("null", listbox1.toWidget().get("selectedIndex"));
		assertEquals("null", listbox2.toWidget().get("selectedIndex"));
		assertEquals("null", listbox3.toWidget().get("selectedIndex"));
		assertEquals("", lb1.text());
		assertEquals("1", listbox4.toWidget().get("selectedIndex"));
		assertEquals("1", listbox5.toWidget().get("selectedIndex"));
		assertEquals("1", listbox6.toWidget().get("selectedIndex"));
		assertEquals("1", lb2.text());

		click(listbox1.find("@listitem"));
		waitResponse();
		assertEquals("0", listbox2.toWidget().get("selectedIndex"));
		assertEquals("0", listbox3.toWidget().get("selectedIndex"));
		assertEquals("0", lb1.text());

		click(listbox2.find("@listitem").eq(1));
		waitResponse();
		assertEquals("1", listbox1.toWidget().get("selectedIndex"));
		assertEquals("1", listbox3.toWidget().get("selectedIndex"));
		assertEquals("1", lb1.text());

		click(listbox3.find("@listitem").eq(2));
		waitResponse();
		assertEquals("2", listbox1.toWidget().get("selectedIndex"));
		assertEquals("2", listbox2.toWidget().get("selectedIndex"));
		assertEquals("2", lb1.text());

		click(listbox4.find("@listitem").eq(0));
		waitResponse();
		String selIndex = listbox4.toWidget().get("selectedIndex");
		assertNotEquals(selIndex, listbox5.toWidget().get("selectedIndex"));
		assertNotEquals(selIndex, listbox6.toWidget().get("selectedIndex"));
		click(listbox5.find("@listitem").eq(2));
		waitResponse();
		selIndex = listbox5.toWidget().get("selectedIndex");
		assertNotEquals(selIndex, listbox4.toWidget().get("selectedIndex"));
		assertNotEquals(selIndex, listbox6.toWidget().get("selectedIndex"));
		click(listbox6.find("@listitem").eq(3));
		waitResponse();
		selIndex = listbox6.toWidget().get("selectedIndex");
		assertNotEquals(selIndex, listbox4.toWidget().get("selectedIndex"));
		assertNotEquals(selIndex, listbox5.toWidget().get("selectedIndex"));
	}
}
