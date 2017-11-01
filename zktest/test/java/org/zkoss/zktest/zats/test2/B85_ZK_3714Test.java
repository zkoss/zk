package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.*;

/**
 * @author bob peng
 */
public class B85_ZK_3714Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button:eq(0)"));
		waitResponse();
		click(jq(".z-button:eq(1)"));
		waitResponse();
		String bugResult = jq(".z-label:eq(1)").text();
		assertEquals(bugResult, jq(".z-label:eq(2)").text());
		assertNotSame("null", bugResult);
		assertNotNull(bugResult);
	}
}