package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01188MixingELWithRefAllTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery items = jq("$halyout1").find("@label");
		assertEquals("0.Item 1", items.eq(0).text());
		assertEquals("1.Item 2", items.eq(1).text());
		
		items = jq("$listbox1").find("@listitem");
		assertEquals("0.Item 1", items.eq(0).find("@listcell > @label").text());
		assertEquals("1.Item 2", items.eq(1).find("@listcell > @label").text());
		
		items = jq("$grid1").find("@row");
		assertEquals("0.Item 1", items.eq(0).find("@label").text());
		assertEquals("1.Item 2", items.eq(1).find("@label").text());
		
		click(jq("$combobox1 .z-combobox-button"));
		waitResponse();
		items = jq("$combobox1").find("@comboitem");
		assertEquals("0.Item 1", items.eq(0).toWidget().get("label"));
		assertEquals("1.Item 2", items.eq(1).toWidget().get("label"));
		
		items = jq("$radiogroup1").find("@radio");
		assertEquals("0.Item 1", items.eq(0).toWidget().get("label"));
		assertEquals("1.Item 2", items.eq(1).toWidget().get("label"));
		
		items = jq("$tree1").find("@treecell");
		assertEquals("0.Item 1", items.eq(0).toWidget().get("label"));
		assertEquals("0.Item 1-1", items.eq(1).toWidget().get("label"));
		assertEquals("1.Item 1-2", items.eq(2).toWidget().get("label"));
		assertEquals("1.Item 2", items.eq(3).toWidget().get("label"));
	}
}
