package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00722Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery t21 = jq("$t21");
		JQuery m21 = jq("$m21");
		JQuery cmd1 = jq("$cmd1");
		JQuery cmd2 = jq("$cmd2");

		assertEquals("abc", l11.text());
		assertEquals("abc", t21.val());
		assertEquals("", m21.text());

		type(t21, "efg");
		waitResponse();
		assertEquals("abc", l11.text());
		assertEquals("efg", t21.val());
		assertEquals("the value has to be 'abc' or 'ABC'", m21.text());

		click(cmd1);
		waitResponse();
		assertEquals("abc", l11.text());
		assertEquals("efg", t21.val());
		assertEquals("the value has to be 'abc' or 'ABC'", m21.text());

		type(t21, "ABC");
		waitResponse();
		assertEquals("abc", l11.text());
		assertEquals("ABC", t21.val());
		assertEquals("", m21.text());

		click(cmd1);
		waitResponse();
		assertEquals("ABC:saved", l11.text());
		assertEquals("ABC", t21.val());
		assertEquals("", m21.text());

		type(t21, "kkk");
		waitResponse();
		assertEquals("ABC:saved", l11.text());
		assertEquals("kkk", t21.val());
		assertEquals("the value has to be 'abc' or 'ABC'", m21.text());

		click(cmd2);
		waitResponse();
		assertEquals("ABC:saved", l11.text());
		assertEquals("ABC:saved", t21.val());
		assertEquals("", m21.text());
	}
}
