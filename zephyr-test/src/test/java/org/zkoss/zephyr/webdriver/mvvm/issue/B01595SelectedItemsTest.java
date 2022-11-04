package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01595SelectedItemsTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("@listbox");
		JQuery lb = jq("$lb");
		JQuery items = listbox.find("@listitem");

		assertEquals(10, items.length());
		assertEquals("[]", lb.text());

		click(items.eq(2));
		waitResponse();
		assertEquals("[2]", lb.text());

		click(items.eq(3));
		waitResponse();
		click(items.eq(9));
		waitResponse();
		assertEquals("[2, 3, 9]", lb.text());

		click(items.eq(7));
		waitResponse();
		click(items.eq(5));
		waitResponse();
		click(items.eq(1));
		waitResponse();
		assertEquals("[2, 3, 9, 7, 5, 1]", lb.text());

		click(items.eq(5).find(".z-listitem-checkbox"));
		waitResponse();
		click(items.eq(3).find(".z-listitem-checkbox"));
		waitResponse();
		click(items.eq(0));
		waitResponse();
		assertEquals("[2, 9, 7, 1, 0]", lb.text());
	}
}
