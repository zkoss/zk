/* EL3Test.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.el;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 */
public class EL3Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		assertEquals("Hi, Dennis Watson", desktopAgent.query("#result1").as(Label.class).getValue());
		//[Step 2]
		assertEquals("6", desktopAgent.query("#result2").as(Label.class).getValue());
		//[Step 3]
		assertEquals("10000.0", desktopAgent.query("#tb3").as(Textbox.class).getValue());
		//[Step 4]
		List<ComponentAgent> listitems = desktopAgent.queryAll("#lb4 listitem");
		assertEquals(3, listitems.size());
		assertTrue(listitems.get(0).as(Listitem.class).getLabel().contains("John"));
		assertTrue(listitems.get(1).as(Listitem.class).getLabel().contains("John"));
		assertTrue(listitems.get(2).as(Listitem.class).getLabel().contains("John"));
		//[Step 5]
		assertEquals("10", desktopAgent.query("#result5").as(Label.class).getValue());
		//[Step 6]
		assertEquals("4.0", desktopAgent.query("#result6").as(Label.class).getValue());
	}
}
