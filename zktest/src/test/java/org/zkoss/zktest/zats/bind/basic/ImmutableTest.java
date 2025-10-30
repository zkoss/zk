package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class ImmutableTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/immutable.zul");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery l3 = jq("$l3");
		JQuery l4 = jq("$l4");
		JQuery l5 = jq("$l5");
		JQuery cmd1 = jq("$cmd1");
		JQuery cmd2 = jq("$cmd2");

		assertEquals("A", l1.toWidget().get("value"));
		assertEquals("A-option", l2.toWidget().get("value"));
		assertEquals("A-option", l3.toWidget().get("value"));
		assertEquals("A-option", l4.toWidget().get("value"));
		assertEquals("A-option", l5.toWidget().get("value"));

		click(cmd1.toWidget());
		waitResponse();
		assertEquals("A", l1.toWidget().get("value"));
		assertEquals("A-option", l2.toWidget().get("value"));
		assertEquals("A-option", l3.toWidget().get("value"));
		assertEquals("A-option", l4.toWidget().get("value"));
		assertEquals("A-option", l5.toWidget().get("value"));

		click(cmd2.toWidget());
		waitResponse();
		assertEquals("A", l1.toWidget().get("value"));
		assertEquals("A-option", l2.toWidget().get("value"));
		assertEquals("A-option", l3.toWidget().get("value"));
		assertEquals("B-option", l4.toWidget().get("value"));
		assertEquals("B-option", l5.toWidget().get("value"));
	}
}
