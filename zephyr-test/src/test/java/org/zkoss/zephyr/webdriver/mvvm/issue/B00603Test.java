package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00603Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery outsidebox = jq("$outsidebox");
		assertEquals(4, outsidebox.toWidget().nChildren());
		String[] itemLabel = {"A", "B", "C"};
		String[] optionLabel = {"A", "B"};
		for (int i = 0; i < 3; i++) {
			JQuery outeritem = outsidebox.find("> @listitem").eq(i);
			String outerl = itemLabel[i];
			assertEquals(outerl, outeritem.find(".z-listcell-content").eq(0).text());
			JQuery innerbox = outeritem.find("@listbox");
			assertTrue(innerbox.length() > 0);

			JQuery inneritems = innerbox.find("@listitem");
			assertEquals(2, inneritems.length());

			for (int j = 0; j < 2; j++) {
				String innerl = optionLabel[j];
				assertEquals(outerl + " " + innerl, inneritems.eq(j).find(".z-listcell-content").eq(0).text());
				assertEquals(outerl + " " + innerl + innerl, inneritems.eq(j).find(".z-listcell-content").eq(1).text());
			}
		}
	}
}
