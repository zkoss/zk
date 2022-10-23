package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

public class B60_ZK_919Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 4; i++) {
			click(jq(".z-datebox:eq(" + i + ")").find(".z-datebox-button"));
			waitResponse();
			click(jq(".z-calendar:eq(" + i + ") .z-calendar-cell:contains(14)"));
			waitResponse();
		}
		assertFalse(jq(".z-errorbox").exists(), "should not see any error message.");
	}
}