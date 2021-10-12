package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B50_ZK_428Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$button"));
		sleep(20000);
		assertTrue(
				"it should not take more than 20 seconds to run on the client side for change the size of ListModelList",
				jq(".z-row").length() >= 50);
	}
}