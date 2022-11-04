package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00678Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");

		assertEquals("Value A", l1.text());
		assertEquals("msg A", l2.text());

		JQuery btn1 = jq("$btn1");
		click(btn1);
		waitResponse();

		assertEquals("Value B", l1.text());
		assertEquals("msg B", l2.text());

		JQuery btn2 = jq("$btn2");
		click(btn2);
		waitResponse();

		assertEquals("Value C", l1.text());
		assertEquals("msg C", l2.text());
	}
}
