package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author bob peng
 */
public class B85_ZK_3582Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int tabindexCnt = jq("[tabindex]").length();
		assertEquals("error popped", 8, tabindexCnt);
	}
}