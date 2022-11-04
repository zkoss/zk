package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01005BeanValidatorPathTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");
		JQuery l1 = jq("$l1");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");
		JQuery update = jq("$update");
		JQuery msg = jq("$msg");

		assertEquals("A", t1.val());

		type(t1, "Aa");
		waitResponse();
		assertEquals("min length is 3", msg1.text());
		assertEquals("A", l1.text());

		type(t1, "Aab");
		waitResponse();
		assertEquals("", msg1.text());
		assertEquals("Aab", l1.text());
		assertEquals("A", t2.val());

		type(t2, "Ab");
		waitResponse();
		assertEquals("min length is 3", msg2.text());
		assertEquals("Aab", l1.text());

		type(t2, "Abc");
		waitResponse();
		assertEquals("", msg2.text());
		assertEquals("Aab", l1.text());

		click(update);
		waitResponse();
		assertEquals("Abc", t1.val());
		assertEquals("Abc", l1.text());
		assertEquals("update value1:Abc", msg.text());
	}
}
