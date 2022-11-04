package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00775ListmodelSelectionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery listbox = jq("$listbox");
		JQuery header = jq("$header");
		JQuery shrink = jq("$shrink");

		click(header);
		waitResponse();
		click(listbox.find("@listitem").eq(8).eq(0));
		waitResponse();
		assertEquals("8", listbox.toWidget().get("selectedIndex"));

		click(shrink);
		waitResponse();
		assertEquals("0", listbox.toWidget().get("selectedIndex"));
	}
}
