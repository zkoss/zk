package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01960BindArrayTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery lb1 = jq("$lb1");
		JQuery tb2 = jq("$tb2");
		JQuery lb2 = jq("$lb2");
		JQuery tb3 = jq("$tb3");
		JQuery lb3 = jq("$lb3");
		JQuery tb4 = jq("$tb4");
		JQuery lb4 = jq("$lb4");

		assertEquals("This", lb1.text());
		assertEquals("is", lb2.text());
		assertEquals("a", lb3.text());
		assertEquals("Test", lb4.text());

		type(tb1, "yo");
		waitResponse();
		assertEquals("yo", lb1.text());
		type(tb2, "yoo");
		waitResponse();
		assertEquals("yoo", lb2.text());
		type(tb3, "yooo");
		waitResponse();
		assertEquals("yooo", lb3.text());
		type(tb4, "yoooo");
		waitResponse();
		assertEquals("yoooo", lb4.text());

	}
}
