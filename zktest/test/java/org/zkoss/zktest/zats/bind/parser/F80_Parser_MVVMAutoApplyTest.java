package org.zkoss.zktest.zats.bind.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class F80_Parser_MVVMAutoApplyTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent w1 = desktop.query("#root #w1");
		Object applyAttr1 = w1.as(Window.class).getAttribute("$composer");
		assertNotNull(applyAttr1);
		assertTrue(applyAttr1.toString().indexOf("org.zkoss.bind.BindComposer") != -1);
		ComponentAgent l1 = desktop.query("#root #w1").getChild(0);
		assertEquals(l1.as(Label.class).getValue(),"test1");
		
		ComponentAgent w2 = desktop.query("#root #w1 #w2");
		Object applyAttr2 = w2.as(Window.class).getAttribute("$composer");
		assertNotNull(applyAttr2);
		assertTrue(applyAttr2.toString().indexOf("org.zkoss.bind.BindComposer") != -1);
		ComponentAgent l2 = desktop.query("#root #w1 #w2").getChild(0);
		assertEquals(l2.as(Label.class).getValue(),"test2");
		
		ComponentAgent w3 = desktop.query("#root #w3");
		Object applyAttr3 = w3.as(Window.class).getAttribute("$composer");
		assertNotNull(applyAttr3);
		assertTrue(applyAttr3.toString().indexOf("org.zkoss.bind.BindComposer") != -1);
		ComponentAgent l3 = desktop.query("#root #w3").getChild(0);
		assertEquals(l3.as(Label.class).getValue(),"test3");
	}
}
