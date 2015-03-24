package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

public class B70_ZK_2567Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			connect();
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
		
	}
}
