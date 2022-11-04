package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01791GlobalCommandTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lab1 = jq("$lb1");
		JQuery btn = jq("$btn1");

		click(btn);
		waitResponse();
		assertEquals("global: onClick, global", lab1.text());
	}
}
