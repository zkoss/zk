/* B95_ZK_4311Test.java

	Purpose:

	Description:

	History:
		Wed Jan 13 12:09:07 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tree;

public class B95_ZK_4311Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Assert.assertTrue(desktop.query("#lb1").as(Listbox.class).isMultiple());
		Assert.assertTrue(desktop.query("#lb2").as(Listbox.class).isMultiple());
		Assert.assertTrue(desktop.query("#tree1").as(Tree.class).isMultiple());
		Assert.assertTrue(desktop.query("#tree2").as(Tree.class).isMultiple());
	}
}
