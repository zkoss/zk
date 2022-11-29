package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01698TreePerformanceTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(1000);
		assertTrue(jq("@treerow").length() > 0);
	}
}
