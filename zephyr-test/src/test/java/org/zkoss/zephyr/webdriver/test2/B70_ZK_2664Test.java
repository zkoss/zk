package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B70_ZK_2664Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("button").eq(1)); //click second button
		waitResponse();
		assertFalse(jq("input").eq(0).is(":focus")); //check the first input widget should NOT have focus
	}
}
