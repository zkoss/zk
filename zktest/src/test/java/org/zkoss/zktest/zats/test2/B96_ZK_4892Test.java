/* B96_ZK_4892Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jul 14 11:30:11 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B96_ZK_4892Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		Label l1 = desktopAgent.query("#l1").as(Label.class);
		Label l2 = desktopAgent.query("#l2").as(Label.class);
		Label l3 = desktopAgent.query("#l3").as(Label.class);
		Label l4 = desktopAgent.query("#l4").as(Label.class);
		Label l5 = desktopAgent.query("#l5").as(Label.class);
		Label l6 = desktopAgent.query("#l6").as(Label.class);
		String l1Val = l1.getValue();
		String l2Val = l2.getValue();
		String l3Val = l3.getValue();
		String l4Val = l4.getValue();
		String l5Val = l5.getValue();

		desktopAgent.query("#cmd1").click();
		assertEquals(l1Val, l1.getValue());
		assertEquals(l2Val, l2.getValue());
		assertEquals(l3Val, l3.getValue());
		assertEquals(l4Val, l4.getValue());
		assertEquals(l5Val, l5.getValue());
		assertEquals("info - cmd1", l6.getValue());

		desktopAgent.query("#cmd2").click();
		assertEquals(l1Val, l1.getValue());
		assertEquals(l2Val, l2.getValue());
		assertEquals(l3Val, l3.getValue());
		assertEquals("B-option", l4.getValue());
		assertEquals("B-option", l5.getValue());
		assertEquals("info - cmd2", l6.getValue());

		desktopAgent.query("#cmd3").click();
		assertEquals("info - cmd3", l6.getValue());
	}
}
