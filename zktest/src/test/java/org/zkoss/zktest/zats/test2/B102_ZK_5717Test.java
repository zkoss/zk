package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5717Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-tree-icon"));
		waitResponse();
		assertNoAnyError();
	}
}

