package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F0015Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l13 = jq("$l13");
		JQuery l21 = jq("$l21");
		JQuery l22 = jq("$l22");
		JQuery l23 = jq("$l23");

		assertEquals("A", l11.text());
		assertEquals("B", l12.text());
		assertEquals("C", l13.text());
		assertEquals("", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l23.text());

		click(jq("$btn1"));
		waitResponse();
		assertEquals("doCommand1", l11.text());
		assertEquals("B", l12.text());
		assertEquals("C", l13.text());
		assertEquals("doCommand1", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l23.text());

		click(jq("$btn2"));
		waitResponse();
		assertEquals("doCommand1", l11.text());
		assertEquals("doCommand2", l12.text());
		assertEquals("doCommand3", l13.text());
		assertEquals("doCommand1", l21.text());
		assertEquals("doCommand2", l22.text());
		assertEquals("doCommand3", l23.text());
	}
}
