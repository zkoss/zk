package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4014Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertFalse(jq("#zk_log").exists(), "ZK log shouldn't exist");
		sleep(100); // In case of Vue.js rendering
		assertEquals("0369test3test4", jq("@fragment div").text());
	}
}
