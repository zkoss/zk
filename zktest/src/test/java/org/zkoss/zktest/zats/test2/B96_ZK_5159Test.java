package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class B96_ZK_5159Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		final int scrollbarHeight = jq(".z-frozen-inner").height();
		assertTrue(scrollbarHeight >= 2);
	}
}
