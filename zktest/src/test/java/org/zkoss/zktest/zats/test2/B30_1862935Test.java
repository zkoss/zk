package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B30_1862935Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(jq("@tree").isVisible());
	}
}