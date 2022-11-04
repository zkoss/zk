package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00967GroupModelListboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$listbox");
		JQuery groups = listbox.find("@listgroup");
		JQuery groupfoots = listbox.find("@listgroupfoot");
		JQuery items = listbox.find("@listitem");
		JQuery l1 = jq("$l1");
		JQuery sel1 = jq("$sel1");
		JQuery sel2 = jq("$sel2");

		assertEquals(3, groups.length());
		assertEquals(3, groupfoots.length());
		assertEquals(5, items.length());

		assertEquals("Fruits", groups.eq(0).toWidget().get("label"));
		assertEquals("Seafood", groups.eq(1).toWidget().get("label"));
		assertEquals("Vegetables", groups.eq(2).toWidget().get("label"));

		assertEquals("1", groupfoots.eq(0).find(".z-listcell-content").eq(0).text());
		assertEquals("2", groupfoots.eq(1).find(".z-listcell-content").eq(0).text());
		assertEquals("2", groupfoots.eq(2).find(".z-listcell-content").eq(0).text());

		assertEquals("Apples", items.eq(0).find("@label").eq(1).text());
		assertEquals("Salmon", items.eq(1).find("@label").eq(1).text());
		assertEquals("Shrimp", items.eq(2).find("@label").eq(1).text());
		assertEquals("Asparagus", items.eq(3).find("@label").eq(1).text());
		assertEquals("Beets", items.eq(4).find("@label").eq(1).text());

		click(items.eq(4));
		waitResponse();
		assertEquals("Beets", l1.text());

		click(sel1);
		waitResponse();
		assertEquals("Apples", l1.text());

		click(sel2);
		waitResponse();
		assertEquals("Salmon", l1.text());
	}
}
