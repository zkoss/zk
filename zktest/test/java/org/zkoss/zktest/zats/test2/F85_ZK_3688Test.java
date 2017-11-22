package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author bob peng
 */
public class F85_ZK_3688Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button:eq(0)"));
		waitResponse();
		click(jq(".z-button:eq(1)"));
		waitResponse();
		click(jq(".z-button:eq(2)"));
		waitResponse();
		click(jq(".z-button:eq(3)"));
		waitResponse();
		String s = getZKLog();
		assertEquals("Unexpected z-index value.", "2001 5000 5001 10000", getZKLog());
	}
}