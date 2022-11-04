package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01547NPEWhenCreateNonPageTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btn1 = jq("$btn1");
		click(btn1);
		waitResponse();
		assertEquals("A", jq("$cnt $win $lb").text());
	}

	@Test
	public void testB() {
		connect();
		JQuery btn2 = jq("$btn2");
		click(btn2);
		waitResponse();
		assertEquals("A", jq("$cnt $win $lb").text());
	}
}
