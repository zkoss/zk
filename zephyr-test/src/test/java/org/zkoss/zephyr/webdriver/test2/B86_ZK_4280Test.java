package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4280Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("false", jq(".z-window-header").text());
		JQuery btn = jq("$toggle");
		JQuery de = jq("$detach");
		click(btn);
		waitResponse();
		click(de);
		waitResponse();
		click(btn);
		waitResponse();
		assertEquals("true", jq(".z-window-header").text());
	}
}
