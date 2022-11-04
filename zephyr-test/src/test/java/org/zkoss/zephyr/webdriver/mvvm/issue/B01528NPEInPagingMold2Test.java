package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01528NPEInPagingMold2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$listbox");

		click(jq("$btn1"));
		waitResponse();
		assertEquals("Item 0 Updated", listbox.find("@listitem").eq(0).find(".z-listcell-content").eq(0).text());

		click(jq("$btn2"));
		waitResponse();
		assertEquals("Item 2 Updated", listbox.find("@listitem").eq(2).find(".z-listcell-content").eq(0).text());

		click(jq("$btn3"));
		waitResponse();
		assertEquals("Item 5 Updated", listbox.find("@listitem").eq(5).find(".z-listcell-content").eq(0).text());

		click(jq("$btn4"));
		waitResponse();
		assertEquals("Item 9 Updated", listbox.find("@listitem").eq(9).find(".z-listcell-content").eq(0).text());
	}
}
