package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00757OnChangeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery t1 = jq("$t1");
		JQuery l1 = jq("$l1");
		JQuery t2 = jq("$t2");
		JQuery l2 = jq("$l2");
		JQuery t3 = jq("$t3");
		JQuery l31 = jq("$l31");
		JQuery l32 = jq("$l32");
		JQuery t4 = jq("$t4");
		JQuery l4 = jq("$l4");

		type(t1, "A");
		waitResponse();
		assertEquals("A-X", l1.text());

		type(t2, "A");
		waitResponse();
		assertEquals("null-Y", l2.text());

		type(t2, "B");
		waitResponse();
		assertEquals("B-Y", l2.text());

		type(t2, "C");
		waitResponse();
		assertEquals("B-Y", l2.text());

		type(t3, "A");
		waitResponse();
		assertEquals("A", l31.text());
		assertEquals("", l32.text());
		assertEquals("", l4.text());

		type(t4, "C");
		waitResponse();
		assertEquals("A", l31.text());
		assertEquals("", l32.text());
		assertEquals("", l4.text());

		type(t3, "B");
		waitResponse();
		assertEquals("B", l31.text());
		assertEquals("B-I", l32.text());
		assertEquals("C-J", l4.text());
	}
}
