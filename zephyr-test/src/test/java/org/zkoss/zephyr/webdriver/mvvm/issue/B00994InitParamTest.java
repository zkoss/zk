package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00994InitParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$win1 $l1");
		JQuery l2 = jq("$win2 $l2");

		assertEquals("foo", l1.text());
		assertEquals("bar", l2.text());
	}
}
