package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class ExecutionparamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn1").toWidget());
		waitResponse();
		assertEquals("foo", jq("$w1").find("$l11").toWidget().get("value"));
		assertEquals("bar", jq("$w1").find("$l12").toWidget().get("value"));
		click(jq("$w1").find("$cmd1").toWidget());
		waitResponse();
		assertEquals("", jq("$w1").find("$l11").toWidget().get("value"));
		assertEquals("", jq("$w1").find("$l12").toWidget().get("value"));
		click(jq("$btn2").toWidget());
		waitResponse();
		assertEquals("abc", jq("$w2").find("$l11").toWidget().get("value"));
		assertEquals("goo", jq("$w2").find("$l12").toWidget().get("value"));
		click(jq("$w2").find("$cmd1").toWidget());
		waitResponse();
		assertEquals("", jq("$w2").find("$l11").toWidget().get("value"));
		assertEquals("", jq("$w2").find("$l12").toWidget().get("value"));
	}
}
