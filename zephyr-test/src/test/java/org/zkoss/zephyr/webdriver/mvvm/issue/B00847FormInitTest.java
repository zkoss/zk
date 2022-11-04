package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00847FormInitTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery cmd1 = jq("$cmd1");
		JQuery cmd2 = jq("$cmd2");

		assertEquals("blue", l1.text());
		assertEquals("blue", l2.text());

		click(cmd1);
		waitResponse();
		assertEquals("red", l1.text());
		assertEquals("blue", l2.text());

		click(cmd2);
		waitResponse();
		assertEquals("yellow", l1.text());
		assertEquals("yellow", l2.text());
	}
}
