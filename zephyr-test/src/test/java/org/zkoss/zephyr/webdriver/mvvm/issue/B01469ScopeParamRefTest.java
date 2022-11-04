package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01469ScopeParamRefTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$win1 $l1");
		JQuery l2 = jq("$win1 $inc1 $win2 $l2");
		JQuery l3 = jq("$win1 $inc1 $win2 $l3");

		assertEquals("ABC", l1.text());
		assertEquals("ABC", l2.text());
		assertEquals("ABC", l3.text());
	}
}
