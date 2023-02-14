package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B60_ZK_1178Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertTrue(jq("@image").eq(1).toElement().get("src").startsWith("data:image/png;base64,"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq("@image").eq(2).toElement().get("src").startsWith("data:image/png;base64,"));
	}
}