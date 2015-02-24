package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.PagingAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B70_ZK_2539Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent mytree = desktop.query("#mytree");
		ComponentAgent paging = mytree.query("paging");
		ComponentAgent btn = desktop.query("#btn");
		
		paging.as(PagingAgent.class).moveTo(9999);
		try {
			btn.click();
			assertTrue(true);
		} catch (Exception e) {
			assertFalse(true);
		}
	}
}
