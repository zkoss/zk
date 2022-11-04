package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B02022ComponentRemovalTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery arbtn = jq("$arbtn");
		JQuery btn = jq("$btn");
		JQuery lab = jq("$lab");

		assertEquals("0", lab.text());
		click(arbtn);
		waitResponse();
		assertEquals("1", lab.text());
		click(btn);
		waitResponse();
		assertEquals("2", lab.text());
	}
}
