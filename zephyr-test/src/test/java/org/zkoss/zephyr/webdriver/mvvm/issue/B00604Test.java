package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00604Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery inc1 = jq("$inc1");
		JQuery inc2 = jq("$inc2");
		JQuery listbox1 = inc1.find("@listbox");
		JQuery listbox2 = inc2.find("@listbox");
		String[] itemLabel1 = {"A", "B", "C"};
		String[] itemLabel2 = {"X", "Y", "Z"};

		assertTrue(listbox1.length() > 0);
		assertFalse(listbox2.length() > 0);

		JQuery listbox = listbox1;
		for (int i = 0; i < 2; i++) {
			JQuery items = listbox.find("@listitem");
			assertEquals(3, items.length());
			for (int j = 0; j < items.length(); j++) {
				JQuery item = items.eq(j);
				assertEquals(itemLabel1[j], item.find(".z-listcell-content").eq(0).text());
				assertEquals(itemLabel2[j], item.find(".z-listcell-content").eq(1).text());
			}
			JQuery btn1 = jq("$btn1");
			click(btn1);
			waitResponse();
			listbox = inc2.find("@listbox");
		}
	}
}
