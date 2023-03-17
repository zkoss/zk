package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00849ConverterParametersTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery tb1 = jq("$tb1");
		JQuery l21 = jq("$l21");
		JQuery l22 = jq("$l22");
		JQuery tb2 = jq("$tb2");
		JQuery l31 = jq("$l31");
		JQuery l32 = jq("$l32");
		JQuery tb3 = jq("$tb3");
		JQuery cmd1 = jq("$btn1");
		JQuery cmd2 = jq("$btn2");
		JQuery cmd3 = jq("$btn3");

		type(tb1, "A");
		waitResponse();
		click(cmd1);
		waitResponse();
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("A:value1", tb1.val());

		type(tb2, "B");
		waitResponse();
		click(cmd2);
		waitResponse();
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("B:value2", tb2.val());

		type(tb3, "C");
		waitResponse();
		click(cmd3);
		waitResponse();
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("C:value3", tb3.val());
	}
}
