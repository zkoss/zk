package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01044DeferOnChangeTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery tb2 = jq("$tb2");
		JQuery tb3 = jq("$tb3");
		JQuery tb4 = jq("$tb4");
		JQuery lb2 = jq("$lb2");
		JQuery lb3 = jq("$lb3");
		JQuery lb4 = jq("$lb4");
		JQuery lb5 = jq("$lb5");
		JQuery lb6 = jq("$lb6");
		JQuery save2 = jq("$save2");

		assertEquals("B", tb2.val());
		assertEquals("B", lb2.text());
		assertEquals("C", tb3.val());
		assertEquals("C", lb3.text());
		assertEquals("D", tb4.val());
		assertEquals("D", lb4.text());
		assertEquals("C", lb5.text());
		assertEquals("D", lb6.text());

		type(tb2, "q");
		waitResponse();
		type(tb3, "w");
		waitResponse();
		type(tb4, "e");
		waitResponse();
		assertEquals("q", tb2.val());
		assertEquals("B", lb2.text());
		assertEquals("w", tb3.val());
		assertEquals("C", lb3.text());
		assertEquals("e", tb4.val());
		assertEquals("D", lb4.text());
		assertEquals("C", lb5.text());
		assertEquals("D", lb6.text());

		click(save2);
		waitResponse();
		assertEquals("q", tb2.val());
		assertEquals("q", lb2.text());
		assertEquals("w", tb3.val());
		assertEquals("w", lb3.text());
		assertEquals("e", tb4.val());
		assertEquals("e", lb4.text());
		assertEquals("w", lb5.text());
		assertEquals("e", lb6.text());
	}
}
