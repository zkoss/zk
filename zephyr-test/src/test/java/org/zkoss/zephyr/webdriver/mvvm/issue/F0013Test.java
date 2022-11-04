package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F0013Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");

		assertEquals("A", l1.text());
		assertEquals("B", l2.text());
		assertEquals("A", t1.val());
		assertEquals("B", t2.val());

		type(t1, "Dennis");
		waitResponse();
		type(t2, "Chen");
		waitResponse();
		click(jq("$btn1"));
		waitResponse();
		assertEquals("Dennis-cmd1", l1.text());
		assertEquals("Chen-cmd1", l2.text());
		assertEquals("Dennis", t1.val());
		assertEquals("Chen", t2.val());

		type(t1, "Alice");
		waitResponse();
		type(t2, "Wu");
		waitResponse();
		click(jq("$btn2"));
		waitResponse();
		assertEquals("Alice-cmd2", l1.text());
		assertEquals("Wu-cmd2", l2.text());
		assertEquals("Alice-cmd2", t1.val());
		assertEquals("Wu-cmd2", t2.val());

		type(t1, "Jumper");
		waitResponse();
		type(t2, "Tj");
		waitResponse();
		click(jq("$btn3"));
		waitResponse();
		assertEquals("Jumper-cmd3", l1.text());
		assertEquals("Tj-cmd3", l2.text());
		assertEquals("Jumper-cmd3", t1.val());
		assertEquals("Tj-cmd3", t2.val());
	}
}
