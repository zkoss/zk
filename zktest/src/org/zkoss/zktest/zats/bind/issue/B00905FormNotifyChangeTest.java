package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00905FormNotifyChangeTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent tb = desktop.query("#tb");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent l3 = desktop.query("#l3");
		ComponentAgent btn = desktop.query("#btn");
		ComponentAgent msg = desktop.query("#msg");
		
		assertEquals("Dennis", tb.as(Textbox.class).getValue());
		assertEquals("Dennis", l1.as(Label.class).getValue());
		assertEquals("Dennis", l2.as(Label.class).getValue());
		assertEquals("Dennis", l3.as(Label.class).getValue());
		assertEquals("Dennis", msg.as(Label.class).getValue());
		
		tb.type("Alex");
		assertEquals("Alex", tb.as(Textbox.class).getValue());
		assertEquals("Alex", l1.as(Label.class).getValue());
		assertEquals("Alex", l2.as(Label.class).getValue());
		assertEquals("Alex", l3.as(Label.class).getValue());
		assertEquals("Dennis", msg.as(Label.class).getValue());
		
		btn.click();
		assertEquals("Alex", tb.as(Textbox.class).getValue());
		assertEquals("Alex", l1.as(Label.class).getValue());
		assertEquals("Alex", l2.as(Label.class).getValue());
		assertEquals("Alex", l3.as(Label.class).getValue());
		assertEquals("Alex", msg.as(Label.class).getValue());
	}
}
