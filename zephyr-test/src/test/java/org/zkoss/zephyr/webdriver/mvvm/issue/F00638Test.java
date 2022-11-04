package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00638Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l13 = jq("$l13");
		JQuery t11 = jq("$t11");
		JQuery t12 = jq("$t12");
		JQuery l31 = jq("$l31");
		JQuery t31 = jq("$t31");
		JQuery btn1 = jq("$btn1");

		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		assertEquals("Y2", l13.text());

		type(t11, "a");
		waitResponse();
		assertEquals("a", l11.text());
		type(t12, "b");
		waitResponse();
		assertEquals("b", l12.text());

		assertEquals("C", l31.text());
		assertEquals("D", t31.val());
		click(btn1);
		waitResponse();
		assertEquals("E", l31.text());
		assertEquals("F", t31.val());
	}
}
