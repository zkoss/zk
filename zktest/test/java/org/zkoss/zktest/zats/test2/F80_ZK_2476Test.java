package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

public class F80_ZK_2476Test extends ZATSTestCase {
	protected String getFileLocation() {
		String location = super.getFileLocation();
		return location.substring(0, location.lastIndexOf(".")) + ".zhtml";
	}
	
	@Test
	public void test() {
		try {
			connect();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
