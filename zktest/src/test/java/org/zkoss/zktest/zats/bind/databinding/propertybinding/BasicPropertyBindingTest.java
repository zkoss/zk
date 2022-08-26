/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Thu May 06 18:24:49 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.propertybinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 */
public class BasicPropertyBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		assertEquals("welcome!", desktopAgent.query("#msg1").as(Label.class).getValue());
		//[Step 2] -> web driver
		//[Step 3]
		Label msg3_1 = desktopAgent.query("#msg3_1").as(Label.class);
		Label msg3_2 = desktopAgent.query("#msg3_2").as(Label.class);
		Label msg3_3 = desktopAgent.query("#msg3_3").as(Label.class);
		ComponentAgent tb3_1Agent = desktopAgent.query("#tb3_1");
		Textbox tb3_1 = tb3_1Agent.as(Textbox.class);
		ComponentAgent tb3_2Agent = desktopAgent.query("#tb3_2");
		Textbox tb3_2 = tb3_2Agent.as(Textbox.class);
		ComponentAgent ib3Agent = desktopAgent.query("#ib3");
		Intbox ib3 = ib3Agent.as(Intbox.class);
		assertNotEquals("", msg3_1.getValue());
		assertNotEquals("", msg3_2.getValue());
		assertNotEquals("", msg3_3.getValue());
		assertEquals("", tb3_1.getValue());
		assertEquals("", tb3_2.getValue());
		assertEquals(null, ib3.getValue());
		tb3_1Agent.input("aaa");
		tb3_2Agent.input("bbb");
		ib3Agent.input(12);
		assertEquals("aaa", msg3_1.getValue());
		assertEquals("bbb", msg3_2.getValue());
		assertEquals("12", msg3_3.getValue());
		assertEquals("aaa", tb3_1.getValue());
		assertEquals("bbb", tb3_2.getValue());
		assertEquals(new Integer(12), ib3.getValue());

		//[Step 4]
		Label msg4 = desktopAgent.query("#msg4").as(Label.class);
		ComponentAgent tb4Agent = desktopAgent.query("#tb4");
		Textbox tb4 = tb4Agent.as(Textbox.class);
		assertEquals("123", msg4.getValue());
		assertEquals("123", tb4.getValue());
		tb4Agent.input("321");
		assertEquals("321", msg4.getValue());
		assertEquals("321", tb4.getValue());

		//[Step 5]
		Label msg5 = desktopAgent.query("#msg5").as(Label.class);
		ComponentAgent tb5Agent = desktopAgent.query("#tb5");
		Textbox tb5 = tb5Agent.as(Textbox.class);
		assertEquals("", msg5.getValue());
		assertEquals("", tb5.getValue());
		tb5Agent.input("321");
		assertEquals("321", msg5.getValue());
		assertEquals("321", tb5.getValue());
	}
}
