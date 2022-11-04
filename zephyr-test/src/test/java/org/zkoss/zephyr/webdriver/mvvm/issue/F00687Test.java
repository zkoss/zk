package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00687Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l13 = jq("$l13");
		JQuery l14 = jq("$l14");
		JQuery t11 = jq("$t11");
		JQuery t12 = jq("$t12");
		JQuery t13 = jq("$t13");
		JQuery t14 = jq("$t14");

		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		assertEquals("C", l13.text());
		assertEquals("D", l14.text());

		type(t11, "Q");
		waitResponse();
		assertEquals("Q", l11.text());
		assertEquals("B", l12.text());
		assertEquals("C", l13.text());
		assertEquals("D", l14.text());

		type(t12, "W");
		waitResponse();
		assertEquals("Q", l11.text());
		assertEquals("B", l12.text());
		assertEquals("C", l13.text());
		assertEquals("D", l14.text());

		type(t13, "E");
		waitResponse();
		assertEquals("Q", l11.text());
		assertEquals("W", l12.text());
		assertEquals("E", l13.text());
		assertEquals("D", l14.text());

		click(jq("$btn1"));
		waitResponse();
		assertEquals("Q", l11.text());
		assertEquals("W", l12.text());
		assertEquals("E", l13.text());
		assertEquals("command 1", l14.text());
		assertEquals("command 1", t14.val());
	}
}
