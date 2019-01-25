package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ZATSTestCase;

public class F86_ZK_3540Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(1000);
		Assert.assertFalse(isZKLogAvailable());
	}
}
