package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01060DirectModelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox1 = jq("$listbox1");
		JQuery listbox2 = jq("$listbox2");
		JQuery listbox3 = jq("$listbox3");
		JQuery items1 = listbox1.find("@listitem");
		JQuery items2 = listbox2.find("@listitem");
		JQuery items3 = listbox3.find("@listitem");

		String[] labels = {"A", "B", "C"};
		assertEquals(3, items1.length());
		for (int i = 0; i < items1.length(); i++) {
			assertEquals(labels[i], items1.eq(i).toWidget().get("label"));
		}
		assertEquals(3, items2.length());
		for (int i = 0; i < items2.length(); i++) {
			assertEquals("", items2.eq(i).toWidget().get("label"));
		}
		assertEquals(3, items3.length());
		for (int i = 0; i < items3.length(); i++) {
			assertEquals(labels[i], items3.eq(i).toWidget().get("label"));
		}
	}
}
