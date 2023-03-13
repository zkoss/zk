package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00869SerializationTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery msg = jq("$msg");
		JQuery selected = jq("$win $selected");
		JQuery listbox = jq("$win $listbox");
		JQuery tb1 = jq("$win $tb1");
		JQuery save = jq("$win $save");
		JQuery serialize = jq("$serialize");
		JQuery children = jq("$win $children");

		assertEquals("A", selected.toWidget().get("value"));
		assertEquals("A", tb1.val());
		assertEquals("B", children.find("@label").eq(1).text());

		JQuery items = listbox.find("@listitem");
		click(items.eq(1));
		waitResponse();
		assertEquals("B", selected.toWidget().get("value"));
		assertEquals("B", tb1.val());

		type(tb1, "BX");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("BX", selected.toWidget().get("value"));
		assertEquals("BX", children.find("@label").eq(1).text());
		assertEquals("BX", items.eq(1).find("@listcell").eq(1).toWidget().get("label"));

		click(serialize);
		waitResponse();
		msg = jq("$msg");
		selected = jq("$win $selected");
		listbox = jq("$win $listbox");
		tb1 = jq("$win $tb1");
		save = jq("$win $save");
		serialize = jq("$serialize");
		children = jq("$win $children");
		items = listbox.find("@listitem");
		assertTrue(msg.text().startsWith("done deserialize:"));

		click(items.eq(2));
		waitResponse();
		assertEquals("C", selected.toWidget().get("value"));
		assertEquals("C", tb1.val());

		type(tb1, "CY");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("CY", selected.toWidget().get("value"));
		assertEquals("CY", children.find("@label").eq(2).toWidget().get("value"));
		assertEquals("CY", items.eq(2).find("@listcell").eq(1).toWidget().get("label"));
	}
}
