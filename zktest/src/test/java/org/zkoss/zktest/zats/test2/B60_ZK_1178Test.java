package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B60_ZK_1178Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertTrue(jq("@image").eq(1).toElement().get("src").contains("foo.png"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq("@image").eq(2).toElement().get("src").contains("a.png"));
	}
}