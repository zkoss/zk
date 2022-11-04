package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00762Combobox2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery outerbox = jq("$outerbox");
		JQuery min = jq("$min");
		JQuery clean = jq("$clean");
		JQuery select = jq("$select");
		JQuery showselect = jq("$showselect");

		click(outerbox.find(".z-combobox-button"));
		waitResponse();
		click(jq("@comboitem").eq(0));
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("0", min.text());

		click(outerbox.find(".z-combobox-button"));
		waitResponse();
		click(jq("@comboitem").eq(2));
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("2", min.text());

		click(clean);
		waitResponse();
		assertEquals("", outerbox.find(".z-combobox-input").val());
		click(showselect);
		waitResponse();
		assertEquals("-1", min.text());

		click(select);
		waitResponse();
		assertEquals("B", outerbox.find(".z-combobox-input").val());
		click(showselect);
		waitResponse();
		assertEquals("1", min.text());
	}
}
