package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2747Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(jq("#zk_showBusy").exists());
		sleep(3000);
		assertTrue(!jq("#zk_showBusy").exists());
	}
}
