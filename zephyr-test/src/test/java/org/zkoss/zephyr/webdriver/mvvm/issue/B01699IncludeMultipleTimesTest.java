package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01699IncludeMultipleTimesTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery btn = jq("$btn");
		JQuery lab1 = jq("$lb1");
		JQuery lab2 = jq("$lb2");

		assertEquals("Foo_1", lab1.text());
		assertEquals("Bar_1", lab2.text());

		click(btn);
		waitResponse();
		assertEquals("FOO_1", lab1.text());
		assertEquals("BAR_1", lab2.text());
	}
}
