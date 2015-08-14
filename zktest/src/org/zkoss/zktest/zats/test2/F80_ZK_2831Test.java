/** F80_ZK_2831Test.java.

	Purpose:
		
	Description:
		
	History:
		11:32:58 PM Aug 11, 2015, Created by chunfu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

public class F80_ZK_2831Test extends ZutiBasicTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect(getTestURL("F80-ZK-2831.zul"));
		ComponentAgent lb = desktop.query("#lb");
		ComponentAgent btnInclude = desktop.query("include button");
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent btn2 = desktop.query("#btn2");

		assertEquals("deferred evaluation in label", lb.as(Label.class).getValue());
		assertEquals("deferred evaluation in include", btnInclude.as(Button.class).getLabel());
		btn1.click();
		assertEquals("deferred evaluation in command", lb2.as(Label.class).getValue());

		btn2.click();
		assertEquals("deferred evaluation in label", lb.as(Label.class).getValue());
		assertEquals("deferred evaluation in include", btnInclude.as(Button.class).getLabel());
		assertEquals("deferred evaluation in command", lb2.as(Label.class).getValue());

		//shadow test
		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent lb3 = desktop.query("#lb3");
		ComponentAgent btn3 = desktop.query("#btn3");
		assertEquals(1, getShadowSize(hostAgent));
		assertEquals("deferred evaluation in shadow", lb3.as(Label.class).getValue());
		btn3.click();
		btn2.click();
		lb3 = desktop.query("#lb3"); //retrieve lb3 again because of recreation
		assertEquals("new title in shadow", lb3.as(Label.class).getValue());

		ComponentAgent serialize = desktop.query("#serialize");
		serialize.click();
		assertEquals("deferred evaluation in label", lb.as(Label.class).getValue());
		assertEquals("deferred evaluation in include", btnInclude.as(Button.class).getLabel());
		assertEquals("deferred evaluation in command", lb2.as(Label.class).getValue());
		assertEquals("new title in shadow", lb3.as(Label.class).getValue());
	}
}