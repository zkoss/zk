package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00911FormNotifyChangeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery l3 = jq("$l3");
		JQuery l4 = jq("$l4");
		JQuery l5 = jq("$l5");
		JQuery l6 = jq("$l6");
		JQuery btn = jq("$btn");

		assertEquals("Dennis", l1.text());
		assertEquals("Dennis", l2.text());
		assertEquals("Dennis", l3.text());
		assertEquals("A", l4.text());
		assertEquals("A", l5.text());
		assertEquals("A", l6.text());

		click(btn);
		waitResponse();
		assertEquals("Alex", l1.text());
		assertEquals("Alex", l2.text());
		assertEquals("Alex", l3.text());
		assertEquals("B", l4.text());
		assertEquals("B", l5.text());
		assertEquals("B", l6.text());
	}
}
