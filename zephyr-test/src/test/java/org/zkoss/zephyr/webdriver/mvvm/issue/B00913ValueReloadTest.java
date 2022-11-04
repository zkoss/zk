package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00913ValueReloadTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery l1 = jq("$l1");
		JQuery msg1 = jq("$msg1");

		assertEquals("", msg1.text());

		type(tb1, "abc");
		waitResponse();
		assertEquals("value has to be def", msg1.text());
		assertEquals("abc", tb1.val());
		assertEquals("KGB", l1.text());

		type(tb1, "def");
		waitResponse();
		assertEquals("", msg1.text());
		assertEquals("def", tb1.val());
		assertEquals("def", l1.text());
	}
}
