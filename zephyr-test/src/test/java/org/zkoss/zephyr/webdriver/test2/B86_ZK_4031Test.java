package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4031Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertFalse(jq("#zk_log").exists());
		assertEquals(3, jq("tr").length());
		assertEquals(3, jq("option").length());
	}
}
