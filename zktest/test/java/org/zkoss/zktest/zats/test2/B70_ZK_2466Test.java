package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2466Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertEquals("AAA", jq(".z-column-content").first().text());
	}
}