package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01259HaskMapInFx1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l13 = jq("$l13");
		JQuery l14 = jq("$l14");
		JQuery l15 = jq("$l15");
		JQuery t21 = jq("$t21");
		JQuery t22 = jq("$t22");
		JQuery btn2 = jq("$btn2");
		JQuery t31 = jq("$t31");
		JQuery t32 = jq("$t32");
		JQuery btn3 = jq("$btn3");

		assertEquals("Hello World", l11.text());
		assertEquals("Hello World", l12.text());
		assertEquals("Hello World", l13.text());
		assertEquals("Hi Dennis", l14.text());
		assertEquals("Hi Dennis", l15.text());

		type(t21, "AAA");
		waitResponse();
		type(t22, "BBB");
		waitResponse();
		assertEquals("Hello World", l11.text());
		assertEquals("Hello World", l12.text());
		assertEquals("Hello World", l13.text());
		assertEquals("Hi Dennis", l14.text());
		assertEquals("Hi Dennis", l15.text());

		click(btn2);
		waitResponse();
		assertEquals("AAA", l11.text());
		assertEquals("AAA", l12.text());
		assertEquals("AAA", l13.text());
		assertEquals("BBB", l14.text());
		assertEquals("BBB", l15.text());

		type(t31, "CCC");
		waitResponse();
		type(t32, "DDD");
		waitResponse();
		assertEquals("AAA", l11.text());
		assertEquals("CCC", l12.text());
		assertEquals("AAA", l13.text());
		assertEquals("BBB", l14.text());
		assertEquals("DDD", l15.text());

		click(btn3);
		waitResponse();
		assertEquals("CCC", l11.text());
		assertEquals("CCC", l12.text());
		assertEquals("CCC", l13.text());
		assertEquals("DDD", l14.text());
		assertEquals("DDD", l15.text());
	}
}