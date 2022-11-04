package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01615ChildrenBindingInFormTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery labs1 = jq("$w1 @label");
		JQuery labs2 = jq("$w2 @label");
		JQuery labs3 = jq("$w3 @label");

		assertEquals(3, labs1.length());
		assertEquals(3, labs2.length());
		assertEquals(3, labs3.length());

		assertEquals("A", labs1.eq(0).text());
		assertEquals("B", labs1.eq(1).text());
		assertEquals("C", labs1.eq(2).text());

		assertEquals("D", labs2.eq(0).text());
		assertEquals("E", labs2.eq(1).text());
		assertEquals("F", labs2.eq(2).text());

		assertEquals("X", labs3.eq(0).text());
		assertEquals("Y", labs3.eq(1).text());
		assertEquals("Z", labs3.eq(2).text());
	}
}
