package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.assertTrue;

public class B80_ZK_3265Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent d = connect();
		d.query("#btn").click();
		assertTrue(true);
	}

}
