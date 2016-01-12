/* B80_ZK_3022Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jan  5, 2016  6:22:27 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import junit.framework.Assert;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_3022Test extends ZATSTestCase{

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		//collapsed tree at init, with one item visible
		Assert.assertEquals(1, desktop.queryAll("treeitem").size());
		ComponentAgent btn = desktop.query("button");
		btn.click();
		//expand all nodes, three nodes should be visible
		Assert.assertEquals(3, desktop.queryAll("treeitem").size());
	}
}
