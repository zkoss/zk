package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Form_DirtyTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("false", jq("$dirty").toWidget().get("value"));
		assertEquals("Dennis", jq("$l1").toWidget().get("value"));

		type(jq("$t1").toWidget(), "X");
		waitResponse();
		assertEquals("true", jq("$dirty").toWidget().get("value"));
		assertEquals("X", jq("$l1").toWidget().get("value"));

		type(jq("$t1").toWidget(), "Dennis");
		waitResponse();
		// since ZK 8.0.2, once the value changed, it would become dirty.
		assertEquals("true", jq("$dirty").toWidget().get("value"));
		assertEquals("Dennis", jq("$l1").toWidget().get("value"));

		type(jq("$t1").toWidget(), "Y");
		waitResponse();
		assertEquals("true", jq("$dirty").toWidget().get("value"));
		assertEquals("Y", jq("$l1").toWidget().get("value"));

		click(jq("$btn2").toWidget());
		waitResponse();
		assertEquals("old-name Dennis", jq("$msg").toWidget().get("value"));

		click(jq("$btn1").toWidget());
		waitResponse();
		assertEquals("saved Y", jq("$msg").toWidget().get("value"));

		click(jq("$btn2").toWidget());
		waitResponse();
		assertEquals("old-name Y", jq("$msg").toWidget().get("value"));
		assertEquals("false", jq("$dirty").toWidget().get("value"));
		assertEquals("Y", jq("$l1").toWidget().get("value"));
	}
}
