package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B80_ZK_3562Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			connect().query("#btn").click();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
