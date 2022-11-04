package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01017NestedFormPathTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l21 = jq("$l21");
		JQuery l22 = jq("$l22");
		JQuery l31 = jq("$l31");
		JQuery l32 = jq("$l32");
		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");
		JQuery t3 = jq("$t3");
		JQuery msg = jq("$msg");
		JQuery update = jq("$update");

		assertEquals("A", l11.text());
		assertEquals("B", l21.text());
		assertEquals("C", l31.text());
		assertEquals("A", t1.val());
		assertEquals("B", t2.val());
		assertEquals("C", t3.val());

		type(t1, "Aa");
		waitResponse();
		assertEquals("A", l11.text());
		assertEquals("value is 'Aa'", l12.text());

		type(t2, "Bb");
		waitResponse();
		assertEquals("B", l21.text());
		assertEquals("value is 'Bb'", l22.text());

		type(t3, "Cc");
		waitResponse();
		assertEquals("C", l31.text());
		assertEquals("value is 'Cc'", l32.text());

		click(update);
		waitResponse();
		assertEquals("Aa", l11.text());
		assertEquals("Bb", l21.text());
		assertEquals("Cc", l31.text());
		assertEquals("update value1:Aa,value2:Bb,value3:Cc", msg.text());
	}
}
