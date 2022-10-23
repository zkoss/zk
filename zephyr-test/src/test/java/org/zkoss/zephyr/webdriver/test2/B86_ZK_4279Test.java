package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

public class B86_ZK_4279Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("$chgBtn"));
		waitResponse();
		click(jq("$show"));
		waitResponse();
		click(jq("$chgBtn"));
		waitResponse();
		click(jq("$show"));
		waitResponse();
		click(jq("$logBtn"));
		waitResponse();
		Assertions.assertEquals("1", getZKLog());
	}
}
