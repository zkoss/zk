package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F0002Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery tb2 = jq("$tb2");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");

		assertEquals("A", tb1.val());
		assertEquals("A", l1.text());
		assertEquals("A", tb2.val());
		assertEquals("A", l2.text());

		type(tb1, "XX");
		waitResponse();
		assertEquals("XX", tb1.val());
		assertEquals("XX", l1.text());
		assertEquals("A", tb2.val());
		assertEquals("A", l2.text());

		type(tb2, "YY");
		waitResponse();
		assertEquals("XX", tb1.val());
		assertEquals("XX", l1.text());
		assertEquals("YY", tb2.val());
		assertEquals("A", l2.text());

		click(jq("$btn1"));
		waitResponse();
		assertEquals("YY", tb1.val());
		assertEquals("YY", l1.text());
		assertEquals("YY", tb2.val());
		assertEquals("YY", l2.text());

		JQuery tb3 = jq("$tb3");
		JQuery l31 = jq("$l31");
		JQuery l32 = jq("$l32");
		assertEquals("B", tb3.val());
		assertEquals("B", l31.text());
		assertEquals("B", l32.text());

		type(tb3, "ZZ");
		waitResponse();
		assertEquals("ZZ", tb3.val());
		assertEquals("B", l31.text());
		assertEquals("B", l32.text());

		click(jq("$btn2"));
		waitResponse();
		assertEquals("ZZ", tb3.val());
		assertEquals("ZZ", l31.text());
		assertEquals("ZZ", l32.text());
	}
}
