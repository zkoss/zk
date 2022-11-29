package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class FunctionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("foo", jq("$l11").toWidget().get("value"));
		assertEquals("foo", jq("$l12").toWidget().get("value"));
		assertEquals("foo:2bar", jq("$l13").toWidget().get("value"));
		assertEquals("foo:foo:b", jq("$l14").toWidget().get("value"));
		click(jq("$cmd1").toWidget());
		waitResponse();
		assertEquals("foo0", jq("$l11").toWidget().get("value"));
		assertEquals("foo0", jq("$l12").toWidget().get("value"));
		assertEquals("foo:2bar", jq("$l13").toWidget().get("value"));
		assertEquals("foo0:foo0:b", jq("$l14").toWidget().get("value"));
		click(jq("$cmd2").toWidget());
		waitResponse();
		assertEquals("foo1", jq("$l11").toWidget().get("value"));
		assertEquals("foo1", jq("$l12").toWidget().get("value"));
		assertEquals("foo1:2bar", jq("$l13").toWidget().get("value"));
		assertEquals("foo1:foo1:b", jq("$l14").toWidget().get("value"));
		click(jq("$cmd3").toWidget());
		waitResponse();
		assertEquals("foo1", jq("$l11").toWidget().get("value"));
		assertEquals("foo1", jq("$l12").toWidget().get("value"));
		assertEquals("foo1:2bar", jq("$l13").toWidget().get("value"));
		assertEquals("foo2:foo2:b", jq("$l14").toWidget().get("value"));
	}
}
