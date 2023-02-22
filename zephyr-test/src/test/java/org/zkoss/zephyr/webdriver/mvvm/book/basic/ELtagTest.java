package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class ELtagTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/eltag.zul");
		JQuery t1 = jq("$t1");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");

		type(t1.toWidget(), "AA");
		waitResponse();
		assertEquals("AA:A", l1.toWidget().get("value"));
		assertEquals("AA-B", l2.toWidget().get("value"));

		type(t1.toWidget(), "BB");
		waitResponse();
		assertEquals("BB:A", l1.toWidget().get("value"));
		assertEquals("BB-B", l2.toWidget().get("value"));
	}
}
