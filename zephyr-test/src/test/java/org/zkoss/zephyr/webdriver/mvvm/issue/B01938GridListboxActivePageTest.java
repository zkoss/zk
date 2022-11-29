package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01938GridListboxActivePageTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery intbox = jq("$intbox");
		JQuery grid = jq("$grid");
		JQuery listbox = jq("$listbox");

		assertEquals("3", jq("@listitem").length());
		assertEquals("3", jq("@row").length());

		type(intbox, "23");
		waitResponse();
		assertEquals(23, Integer.parseInt(grid.toWidget().get("activePage")));
		assertEquals(23, Integer.parseInt(listbox.toWidget().get("activePage")));

		type(intbox, "123");
		waitResponse();
		assertEquals(123, Integer.parseInt(grid.toWidget().get("activePage")));
		assertEquals(123, Integer.parseInt(listbox.toWidget().get("activePage")));
	}
}
