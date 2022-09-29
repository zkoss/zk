package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_5159Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		final int scrollbarHeight = jq(".z-frozen-inner").height();
		assertTrue(scrollbarHeight >= 2);
	}
}
