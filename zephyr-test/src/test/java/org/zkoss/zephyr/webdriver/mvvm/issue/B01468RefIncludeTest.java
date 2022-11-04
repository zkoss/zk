package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01468RefIncludeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lb1 = jq("$win1 $lb1");
		JQuery lb2 = jq("$win1 $lb2");
		JQuery lb3 = jq("$win1 $include $win2 $lb3");
		JQuery lb4 = jq("$win1 $include $win2 $lb4");
		JQuery lb5 = jq("$win1 $include $win3 $lb5");
		JQuery lb6 = jq("$win1 $include $win3 $lb6");
		JQuery tb1 = jq("$win1 $tb1");

		assertEquals("ABC", lb1.text());
		assertEquals("ABC", lb2.text());
		assertEquals("ABC", lb3.text());
		assertEquals("ABC", lb4.text());
		assertEquals("ABC", lb5.text());
		assertEquals("ABC", lb6.text());

		type(tb1, "XYZ");
		waitResponse();
		assertEquals("XYZ", lb1.text());
		assertEquals("XYZ", lb2.text());
		assertEquals("XYZ", lb3.text());
		assertEquals("XYZ", lb4.text());
		assertEquals("XYZ", lb5.text());
		assertEquals("XYZ", lb6.text());
	}
}
