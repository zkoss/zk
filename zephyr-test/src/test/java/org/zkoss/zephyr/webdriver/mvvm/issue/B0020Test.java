package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B0020Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		//test property init
		assertEquals(5, jq("@button").length());

		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(4, jq("@button").length());

		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(3, jq("@button").length());

		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(2, jq("@button").length());

		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(1, jq("@button").length());

		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(0, jq("@button").length());
	}
}
