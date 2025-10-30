package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_5414Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		click(jq("@cascader"));
		waitResponse();
		click(jq("li:contains(\"USA\")"));
		waitResponse(3000); // Wait a few seconds for timer to take effect.
		assertTrue(jq("li:contains(\"New York\")").exists());
	}
}
