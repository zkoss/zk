package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01033InitClassTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$win1 $l11");
		JQuery l12 = jq("$win1 $l12");
		JQuery l21 = jq("$win2 $l21");
		JQuery l22 = jq("$win2 $l22");

		assertEquals("", l11.text());
		assertEquals("Chen", l12.text());
		assertEquals("Ian", l21.text());
		assertEquals("Tasi", l22.text());
	}
}
