package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00771_1Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect("/mvvm/issue/F00771.zul");

		JQuery val1 = jq("$val1");
		JQuery val2 = jq("$val2");
		JQuery val3 = jq("$val3");
		JQuery t11 = jq("$t11");
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery t21 = jq("$t21");
		JQuery l21 = jq("$l21");
		JQuery l22 = jq("$l22");
		JQuery t31 = jq("$t31");
		JQuery l31 = jq("$l31");
		JQuery l32 = jq("$l32");
		JQuery reload1 = jq("$reload1");
		JQuery reload2 = jq("$reload2");

		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());

		type(t11, "ab");
		waitResponse();
		type(t11, "ab");
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.text());
		assertEquals("", l21.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.text());
		assertEquals("", l31.text());
		assertEquals("", l32.text());

		type(t21, "de");
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.text());
		assertEquals("value2 must equalsIgnoreCase to def", l21.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.text());
		assertEquals("", l31.text());
		assertEquals("", l32.text());

		type(t31, "xy");
		waitResponse();
		assertEquals("", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.text());
		assertEquals("value2 must equalsIgnoreCase to def", l21.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());

		type(t11, "abc");
		waitResponse();
		assertEquals("abc", val1.text());
		assertEquals("", val2.text());
		assertEquals("", val3.text());
		assertEquals("", l11.text());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l12.text());
		assertEquals("value2 must equalsIgnoreCase to def", l21.text());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());

		type(t21, "def");
		waitResponse();
		assertEquals("abc", val1.text());
		assertEquals("def", val2.text());
		assertEquals("", val3.text());
		assertEquals("", l11.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l12.text());
		assertEquals("", l21.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());

		type(t31, "xyz");
		waitResponse();
		assertEquals("abc", val1.text());
		assertEquals("def", val2.text());
		assertEquals("xyz", val3.text());
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l31.text());
		assertEquals("", l32.text());

		type(t11, "ab");
		waitResponse();
		type(t21, "de");
		waitResponse();
		type(t31, "xy");
		waitResponse();
		assertEquals("abc", val1.text());
		assertEquals("def", val2.text());
		assertEquals("xyz", val3.text());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.text());
		assertEquals("value2 must equalsIgnoreCase to def", l21.text());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());

		click(reload1);
		waitResponse();
		assertEquals("abc", t11.val());
		assertEquals("de", t21.val());
		assertEquals("xy", t31.val());
		assertEquals("", l11.text());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l12.text());
		assertEquals("value2 must equalsIgnoreCase to def", l21.text());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());

		click(reload2);
		waitResponse();
		assertEquals("abc", t11.val());
		assertEquals("def", t21.val());
		assertEquals("xy", t31.val());
		assertEquals("", l11.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l12.text());
		assertEquals("", l21.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l22.text());
		assertEquals("value3 must equalsIgnoreCase to xyz", l31.text());
		assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.text());
	}
}
