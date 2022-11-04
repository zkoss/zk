package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00823RadiogroupModel2Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery box1 = jq("$box1");
		JQuery box2 = jq("$box2");
		JQuery select = jq("$select");
		JQuery clean = jq("$clean");

		assertEquals("-1", l1.text());
		assertEquals("", l2.text());

		JQuery radios = box1.find("@radio");
		click(radios.eq(1));
		waitResponse();
		assertEquals("1", l1.text());
		assertEquals("", l2.text());
		click(radios.eq(3));
		waitResponse();
		assertEquals("3", l1.text());
		assertEquals("", l2.text());

		click(clean);
		waitResponse();
		assertEquals("-1", l1.text());
		assertEquals("", l2.text());

		radios = box2.find("@radio");
		click(radios.eq(1));
		waitResponse();
		assertEquals("-1", l1.text());
		assertEquals("B", l2.text());
		click(radios.eq(3));
		waitResponse();
		assertEquals("-1", l1.text());
		assertEquals("D", l2.text());

		click(clean);
		waitResponse();
		assertEquals("-1", l1.text());
		assertEquals("", l2.text());

		click(select);
		waitResponse();
		assertEquals("0", l1.text());
		assertEquals("A", l2.text());
	}
}
