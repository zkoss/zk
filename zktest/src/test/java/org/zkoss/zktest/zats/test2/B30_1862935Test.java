package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B30_1862935Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(jq("@tree").isVisible());
	}
}