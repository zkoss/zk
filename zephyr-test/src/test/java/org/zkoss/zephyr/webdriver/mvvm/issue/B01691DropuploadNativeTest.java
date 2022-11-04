package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01691DropuploadNativeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lab1 = jq("$lab1");
		JQuery lab2 = jq("$lab2");
		JQuery btn1 = jq("$btn1");
		JQuery btn2 = jq("$btn2");

		click(btn1);
		waitResponse();
		assertEquals("true", lab1.text());
		click(btn2);
		waitResponse();
		assertEquals("native is true", lab2.text());

		click(btn1);
		waitResponse();
		assertEquals("false", lab1.text());
		click(btn2);
		waitResponse();
		assertEquals("native is false", lab2.text());
	}
}
