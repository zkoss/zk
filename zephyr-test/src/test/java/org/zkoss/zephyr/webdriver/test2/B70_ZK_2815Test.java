package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

public class B70_ZK_2815Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@checkbox").eq(2));
		waitResponse();
		assertNoAnyError();
	}
}
