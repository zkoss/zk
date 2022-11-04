package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01895PagingOnListboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tab4 = jq("$tab4");
		JQuery listbox = jq("$listbox");

		assertEquals(0, listbox.length());
		click(tab4);
		waitResponse();
		listbox = jq("$listbox");
		JQuery items = listbox.find("@listitem");
		assertTrue(items.length() > 0);
	}
}
