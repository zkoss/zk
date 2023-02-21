package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Disabled
public class B00632Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");
		JQuery lb3 = jq("$lb3");
		JQuery lb4 = jq("$lb4");
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery t11 = jq("$t11");

		assertEquals("XYZ", lb1.text());
		assertEquals("XYZ", lb2.text());
		assertEquals("XYZ", lb3.text());
		assertEquals("XYZ", lb4.text());
		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		type(t11, "C");
		waitResponse();
		assertEquals("C", l11.text());
		assertEquals("by-C", l12.text());
	}
}
