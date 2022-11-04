package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00634Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery t11 = jq("$t11");
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		assertEquals("A", l11.text());
		assertEquals("B", l12.text());

		type(t11, "PP");
		waitResponse();
		JQuery btn = jq("@button");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");

		click(btn);
		waitResponse();
		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		assertEquals("value 1 has to be XX or ZZ", msg1.text());
		assertEquals("value 2 has to be YY or ZZ", msg2.text());

		type(t11, "XX");
		waitResponse();
		click(btn);
		waitResponse();
		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		assertEquals("", msg1.text());
		assertEquals("value 2 has to be YY or ZZ", msg2.text());

		type(t11, "YY");
		waitResponse();
		click(btn);
		waitResponse();
		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		assertEquals("value 1 has to be XX or ZZ", msg1.text());
		assertEquals("", msg2.text());

		type(t11, "ZZ");
		waitResponse();
		click(btn);
		waitResponse();
		assertEquals("ZZ", l11.text());
		assertEquals("ZZ", l12.text());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());
	}
}
