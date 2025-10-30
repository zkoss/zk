/* RuntimeEvalTest.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.el;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class RuntimeEvalTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		ComponentAgent btn1 = desktopAgent.query("#btn1");
		Label result1Label = desktopAgent.query("#result1").as(Label.class);
		btn1.click();
		assertEquals("command 2 called", result1Label.getValue());
		btn1.click();
		assertEquals("command 1 called", result1Label.getValue());
		btn1.click();
		assertEquals("command 2 called", result1Label.getValue());

		//[Step 2]
		Groupbox gb1 = desktopAgent.query("#gb1").as(Groupbox.class);
		Groupbox gb2 = desktopAgent.query("#gb2").as(Groupbox.class);
		Groupbox gb3 = desktopAgent.query("#gb3").as(Groupbox.class);
		Groupbox gb4 = desktopAgent.query("#gb4").as(Groupbox.class);
		Groupbox gb5 = desktopAgent.query("#gb5").as(Groupbox.class);
		Groupbox gb6 = desktopAgent.query("#gb6").as(Groupbox.class);
		assertFalse(gb1.isVisible());
		assertFalse(gb2.isVisible());
		assertFalse(gb3.isVisible());
		assertFalse(gb4.isVisible());
		assertFalse(gb5.isVisible());
		assertFalse(gb6.isVisible());
		//[Step 3]
		desktopAgent.query("#btn3").click();
		assertFalse(gb1.isVisible());
		assertFalse(gb2.isVisible());
		assertFalse(gb3.isVisible());
		assertFalse(gb4.isVisible());
		assertFalse(gb5.isVisible());
		assertTrue(gb6.isVisible());
		//[Step 4]
		desktopAgent.query("#btn4").click();
		assertTrue(gb1.isVisible());
		assertTrue(gb2.isVisible());
		assertTrue(gb3.isVisible());
		assertTrue(gb4.isVisible());
		assertTrue(gb5.isVisible());
		assertTrue(gb6.isVisible());
		//[Step 5]
		Label result5_1Label = desktopAgent.query("#result5_1").as(Label.class);
		Label result5_2Label = desktopAgent.query("#result5_2").as(Label.class);
		assertEquals("Dennis", result5_1Label.getValue());
		assertEquals(result5_1Label.getValue(), result5_2Label.getValue());
	}
}
