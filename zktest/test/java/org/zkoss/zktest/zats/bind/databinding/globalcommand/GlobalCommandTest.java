/* GlobalCommandTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.globalcommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Vlayout;

public class GlobalCommandTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent defaulGCmdBtn = desktopAgent.query("#defaulGCmdBtn");
		defaulGCmdBtn.click();
		assertEquals("default action", desktopAgent.getZkLog().get(desktopAgent.getZkLog().size() - 1));

		Vlayout mainArea = desktopAgent.query("#mainArea").as(Vlayout.class);
		Vlayout listArea = desktopAgent.query("#listArea").as(Vlayout.class);
		desktopAgent.query("#hide").click();
		assertFalse(mainArea.isVisible());
		assertFalse(listArea.isVisible());
		desktopAgent.query("#show").click();
		assertTrue(mainArea.isVisible());
		assertTrue(listArea.isVisible());

		ComponentAgent iBox = desktopAgent.query("#iBox");
		ComponentAgent addBtn = desktopAgent.query("#addBtn");

		iBox.input("aaa");
		addBtn.click();
		assertEquals(1, desktopAgent.queryAll("#listArea listitem").size());

		iBox.input("bbb");
		addBtn.click();
		assertEquals(2, desktopAgent.queryAll("#listArea listitem").size());
	}
}
