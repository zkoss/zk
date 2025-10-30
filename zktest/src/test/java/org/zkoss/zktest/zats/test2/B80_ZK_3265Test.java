package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B80_ZK_3265Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent d = connect();
		d.query("#btn").click();
		assertTrue(true);
	}

}
