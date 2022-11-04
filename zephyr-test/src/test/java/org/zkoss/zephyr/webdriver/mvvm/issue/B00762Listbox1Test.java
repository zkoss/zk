package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00762Listbox1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery outerbox = jq("$outerbox");
		JQuery selected = jq("$selected");
		JQuery min = jq("$min");
		JQuery clean = jq("$clean");
		JQuery select = jq("$select");
		JQuery showselect = jq("$showselect");

		click(outerbox.find("@listitem").eq(0));
		waitResponse();
		assertEquals("A", selected.toWidget().get("value"));
		click(showselect);
		waitResponse();
		assertEquals("0", min.text());

		click(outerbox.find("@listitem").eq(2));
		waitResponse();
		assertEquals("C", selected.toWidget().get("value"));
		click(showselect);
		waitResponse();
		assertEquals("2", min.text());

		click(clean);
		waitResponse();
		assertEquals("-1", outerbox.toWidget().get("selectedIndex"));
		assertEquals("", selected.toWidget().get("value"));
		click(showselect);
		waitResponse();
		assertEquals("-1", min.text());

		click(select);
		waitResponse();
		assertEquals("1", outerbox.toWidget().get("selectedIndex"));
		assertEquals("B", selected.toWidget().get("value"));
		click(showselect);
		waitResponse();
		assertEquals("1", min.text());
	}
}
