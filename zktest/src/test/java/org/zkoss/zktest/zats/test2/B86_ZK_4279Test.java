package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4279Test extends WebDriverTestCase {
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
