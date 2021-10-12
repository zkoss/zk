/* ModularizedReferenceBindingTest.java
	Purpose:

	Description:

	History:
		Fri May 07 18:05:11 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.referencebinding;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 */
public class ModularizedReferenceBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		Label l1 = desktopAgent.query("include #l1").as(Label.class);
		ComponentAgent ib1 = desktopAgent.query("include #ib1");
		assertEquals("0", l1.getValue());
		assertEquals(0, ib1.as(Intbox.class).getValue().intValue());
		ib1.input(123);
		assertEquals("123", l1.getValue());
		assertEquals(123, ib1.as(Intbox.class).getValue().intValue());
		//[Step 2]
		Label l2 = desktopAgent.query("include #l2").as(Label.class);
		ComponentAgent tb2Agent = desktopAgent.query("include #tb2");
		Textbox tb2 = tb2Agent.as(Textbox.class);
		assertEquals("Dennis", l2.getValue());
		assertEquals("Dennis", tb2.getValue());
		tb2Agent.input("Dennis123");
		assertEquals("Dennis123", l2.getValue());
		assertEquals("Dennis123", tb2.getValue());
		Label l3 = desktopAgent.query("include #l3").as(Label.class);
		ComponentAgent tb3Agent = desktopAgent.query("include #tb3");
		Textbox tb3 = tb3Agent.as(Textbox.class);
		assertEquals("Watson", l3.getValue());
		assertEquals("Watson", tb3.getValue());
		tb3Agent.input("Watson123");
		assertEquals("Watson123", l3.getValue());
		assertEquals("Watson123", tb3.getValue());
		//[Step 3]
		ComponentAgent appendBtn = desktopAgent.query("include #appendBtn");
		appendBtn.click();
		assertEquals("Dennis1231", l2.getValue());
		assertEquals("Dennis1231", tb2.getValue());
		appendBtn.click();
		assertEquals("Dennis12311", l2.getValue());
		assertEquals("Dennis12311", tb2.getValue());
		appendBtn.click();
		assertEquals("Dennis123111", l2.getValue());
		assertEquals("Dennis123111", tb2.getValue());
		//[Step 4]
		desktopAgent.query("include #updateBtn").click();
		assertEquals("Mary", l2.getValue());
		assertEquals("Mary", tb2.getValue());
		assertEquals("King", l3.getValue());
		assertEquals("King", tb3.getValue());
		//[Step 5]
		appendBtn.click();
		assertEquals("Mary1", l2.getValue());
		assertEquals("Mary1", tb2.getValue());
		appendBtn.click();
		assertEquals("Mary11", l2.getValue());
		assertEquals("Mary11", tb2.getValue());
		appendBtn.click();
		assertEquals("Mary111", l2.getValue());
		assertEquals("Mary111", tb2.getValue());
	}
}
