package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B70_ZK_2567Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			connect();
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getCause().toString());
		}
		
	}
}
