/* TreeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 10:51:56 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treeitem;

/**
 * @author rudyhuang
 */
public class TreeTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> treeitem = desktop.queryAll("treeitem");
		final Label selectedLabel = desktop.query("#selectedLabel").as(Label.class);

		treeitem.get(1).select();
		Assert.assertEquals("Root.1", selectedLabel.getValue());
		treeitem.get(0).select();
		Assert.assertEquals("Root.0", selectedLabel.getValue());

		Assert.assertFalse(treeitem.get(0).as(Treeitem.class).isOpen());
		Assert.assertFalse(treeitem.get(1).as(Treeitem.class).isOpen());
		treeitem.get(0).as(OpenAgent.class).open(true);
		Assert.assertTrue(treeitem.get(0).as(Treeitem.class).isOpen());
		Assert.assertTrue(treeitem.get(1).as(Treeitem.class).isOpen());
	}
}
