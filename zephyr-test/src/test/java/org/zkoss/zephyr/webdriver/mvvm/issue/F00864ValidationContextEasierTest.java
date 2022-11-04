package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00864ValidationContextEasierTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");
		JQuery inp1 = jq("$inp1");
		JQuery inp2 = jq("$inp2");
		JQuery save1 = jq("$save1");
		JQuery err = jq("$err");

		assertEquals("", err.text());
		type(inp1, "Dennis");
		waitResponse();
		type(inp2, "100");
		waitResponse();
		click(save1);
		waitResponse();

		assertEquals("", err.text());
		assertEquals("Dennis", msg1.text());
		assertEquals("100", msg2.text());
	}
}
