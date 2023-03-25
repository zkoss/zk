package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01063BindExceptionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery lb1 = jq("$lb1");
		JQuery tb1 = jq("$tb1");
		JQuery lb2 = jq("$lb2");
		JQuery tb2 = jq("$tb2");
		JQuery tb3 = jq("$tb3");
		type(tb1, "1");
		waitResponse();
		assertEquals("1", lb1.text());
		type(tb2, "2");
		waitResponse();
		assertEquals("2", lb2.text());
		String exceptionMsg = "";
		type(tb3, "3");
		waitResponse();
		assertTrue(hasError());
	}
}
