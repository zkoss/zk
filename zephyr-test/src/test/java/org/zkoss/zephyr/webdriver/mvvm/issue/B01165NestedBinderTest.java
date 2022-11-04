package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01165NestedBinderTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery outerPidLb = jq("$outerPidLb");
		JQuery outerPDescLb = jq("$outerPDescLb");
		JQuery pidLb = jq("$pidLb");
		JQuery pDescLb = jq("$pDescLb");
		JQuery vmsSelIdLb = jq("$vmsSelIdLb");
		JQuery vmsSelDescLb = jq("$vmsSelDescLb");

		assertEquals("b3", outerPidLb.text());
		assertEquals("this is b3", outerPDescLb.text());
		assertEquals("b3", pidLb.text());
		assertEquals("this is b3", pDescLb.text());
		assertEquals("b3", vmsSelIdLb.text());
		assertEquals("this is b3", vmsSelDescLb.text());
	}
}
