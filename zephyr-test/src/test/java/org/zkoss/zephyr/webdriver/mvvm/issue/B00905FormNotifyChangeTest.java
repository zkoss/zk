package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00905FormNotifyChangeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tb = jq("$tb");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery l3 = jq("$l3");
		JQuery btn = jq("$btn");
		JQuery msg = jq("$msg");

		assertEquals("Dennis", tb.val());
		assertEquals("Dennis", l1.text());
		assertEquals("Dennis", l2.text());
		assertEquals("Dennis", l3.text());
		assertEquals("Dennis", msg.text());

		type(tb, "Alex");
		waitResponse();
		assertEquals("Alex", tb.val());
		assertEquals("Alex", l1.text());
		assertEquals("Alex", l2.text());
		assertEquals("Alex", l3.text());
		assertEquals("Dennis", msg.text());

		click(btn);
		waitResponse();
		assertEquals("Alex", tb.val());
		assertEquals("Alex", l1.text());
		assertEquals("Alex", l2.text());
		assertEquals("Alex", l3.text());
		assertEquals("Alex", msg.text());
	}
}
