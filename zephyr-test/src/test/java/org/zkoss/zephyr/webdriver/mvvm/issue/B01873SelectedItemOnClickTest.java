package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;


public class B01873SelectedItemOnClickTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$win $listbox");
		JQuery lb1 = jq("$win $lb1");
		JQuery lb2 = jq("$win $lb2");
		JQuery items = listbox.find("@listitem");

		assertEquals(3, items.length());

		click(items.eq(0));
		waitResponse();
		assertEquals("Item 1", lb1.text());
		JQuery divs = items.eq(0).find("@div");
		click(divs.eq(0));
		waitResponse();
		assertEquals("Item 1-sub1", lb2.text());
		click(divs.eq(1));
		waitResponse();
		assertEquals("Item 1-sub2", lb2.text());

		click(items.eq(1));
		waitResponse();
		assertEquals("Item 2", lb1.text());
		divs = items.eq(1).find("@div");
		click(divs.eq(0));
		waitResponse();
		assertEquals("Item 2-sub1", lb2.text());
		click(divs.eq(1));
		waitResponse();
		assertEquals("Item 2-sub2", lb2.text());

		click(items.eq(2));
		waitResponse();
		assertEquals("Item 3", lb1.text());
		divs = items.eq(2).find("@div");
		click(divs.eq(0));
		waitResponse();
		assertEquals("Item 3-sub1", lb2.text());
		click(divs.eq(1));
		waitResponse();
		assertEquals("Item 3-sub2", lb2.text());
	}
}
