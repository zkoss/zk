package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01366RadioSelectedItemTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery rg1 = jq("$radiogroup1");
		JQuery rg2 = jq("$radiogroup2");
		JQuery lb1 = jq("$lb1");

		click(rg1.find("@radio"));
		waitResponse();
		assertEquals(true, rg2.find("@radio").eq(0).toWidget().is("checked"));
		assertEquals(false, rg2.find("@radio").eq(1).toWidget().is("checked"));
		assertEquals(false, rg2.find("@radio").eq(2).toWidget().is("checked"));
		assertEquals("name 0", lb1.text());

		click(rg2.find("@radio").eq(1));
		waitResponse();
		assertEquals(false, rg1.find("@radio").eq(0).toWidget().is("checked"));
		assertEquals(true, rg1.find("@radio").eq(1).toWidget().is("checked"));
		assertEquals(false, rg1.find("@radio").eq(2).toWidget().is("checked"));
		assertEquals("name 1", lb1.text());
	}
}
