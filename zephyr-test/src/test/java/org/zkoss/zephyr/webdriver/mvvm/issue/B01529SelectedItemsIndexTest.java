package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01529SelectedItemsIndexTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$listbox");
		JQuery lb = jq("$lb");
		JQuery items = listbox.find("@listitem");

		click(items.eq(1));
		waitResponse();
		assertEquals("[1]", lb.text());

		click(items.eq(8));
		waitResponse();
		assertEquals("[1, 8]", lb.text());

		click(items.eq(9));
		waitResponse();
		assertEquals("[1, 8, 9]", lb.text());

		click(items.eq(4));
		waitResponse();
		assertEquals("[1, 8, 9, 4]", lb.text());

		click(items.eq(8).find(".z-listitem-checkbox"));
		waitResponse();
		;
		assertEquals("[1, 9, 4]", lb.text());

		click(items.eq(8));
		waitResponse();
		assertEquals("[1, 9, 4, 8]", lb.text());

		click(items.eq(1).find(".z-listitem-checkbox"));
		waitResponse();
		assertEquals("[9, 4, 8]", lb.text());

		click(items.eq(2));
		waitResponse();
		assertEquals("[9, 4, 8, 2]", lb.text());
	}
}
