package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01026BindCustomAttrTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery t2 = jq("$t2");
		JQuery t3 = jq("$t3");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");
		JQuery test1 = jq("$test1");
		JQuery test2 = jq("$test2");


		assertEquals("A", l1.text());
		assertEquals("B", t2.text());
		assertEquals("C", t3.text());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());

		click(test1);
		waitResponse();
		assertEquals("A", l1.text());
		assertEquals("y", t2.text());
		assertEquals("z", t3.text());
		assertEquals("value1:A,value2:B,value3:C", msg1.text());
		assertEquals("", msg2.text());

		click(test2);
		waitResponse();
		assertEquals("A", l1.text());
		assertEquals("y", t2.text());
		assertEquals("z", t3.text());
		assertEquals("value1:A,value2:B,value3:C", msg1.text());
		assertEquals("value1:A,value2:y,value3:z", msg2.text());

	}
}
