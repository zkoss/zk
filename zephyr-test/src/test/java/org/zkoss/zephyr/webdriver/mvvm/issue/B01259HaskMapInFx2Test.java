package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01259HaskMapInFx2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l13 = jq("$l13");
		JQuery l14 = jq("$l14");
		JQuery l15 = jq("$l15");

		assertEquals("Hello World", l11.text());
		assertEquals("Hello World", l12.text());
		assertEquals("Hello World", l13.text());
		assertEquals("Hi Dennis", l14.text());
		assertEquals("Hi Dennis", l15.text());
	}
}
