package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2271Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertTrue(!jq(".z-error").exists());
	}
}