package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B60_ZK_1139Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("input[type*=checkbox]"));
		waitResponse();
		sendKeys(jq(".z-textbox"), "aaaaa");
		waitResponse();
		click(jq(".z-button:contains(change name)"));
		waitResponse();
		assertEquals(jq(".z-row:eq(1) .z-label:eq(1)").text(), "Dennis", "should not changed");
		assertNoAnyError();
	}
}