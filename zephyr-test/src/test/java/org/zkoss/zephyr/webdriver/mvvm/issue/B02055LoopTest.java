package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Disabled
public class B02055LoopTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery grid = jq("$grid");
		JQuery listbox = jq("$listbox");
		String[] links = {"http://www.zkoss.org", "http://jp.zkoss.org", "http://zh.zkoss.org", "http://www.potix.com"};
		JQuery rows = grid.find("@row");
		JQuery items = listbox.find("@listitem");

		assertEquals(4, rows.length());
		assertEquals(4, items.length());
		for (int i = 0; i < rows.length(); i++) {
			assertEquals(links[i], rows.eq(i).find("@label").text());
		}
		for (int i = 0; i < rows.length(); i++) {
			assertEquals(links[i], items.eq(i).find("@label").text());
		}
	}
}
