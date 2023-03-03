package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00892ChildBindingUnderListboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery outerbox = jq("$outerbox");
		JQuery items = outerbox.find("@listitem");
		assertEquals(4, items.length());

		JQuery cell = items.eq(0).find("@listcell");
		assertEquals(2, cell.length());
		assertEquals("0", cell.find(".z-listcell-content").eq(0).text());
		assertEquals("A", cell.find(".z-listcell-content").eq(1).text());

		cell = items.eq(1).find("@listcell");
		assertEquals(2, cell.length());
		assertEquals("1", cell.find(".z-listcell-content").eq(0).text());
		assertEquals("B", cell.find(".z-listcell-content").eq(1).text());

		cell = items.eq(2).find("@listcell");
		assertEquals(2, cell.length());
		assertEquals("2", cell.find(".z-listcell-content").eq(0).text());
		assertEquals("C", cell.find(".z-listcell-content").eq(1).text());

		cell = items.eq(3).find("@listcell");
		assertEquals(2, cell.length());
		assertEquals("3", cell.find(".z-listcell-content").eq(0).text());
		assertEquals("D", cell.find(".z-listcell-content").eq(1).text());
	}
}
