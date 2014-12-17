package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00913ValueReloadTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent msg1 = desktop.query("#msg1");
		
		assertEquals("", msg1.as(Label.class).getValue());
		
		tb1.type("abc");
		assertEquals("value has to be def", msg1.as(Label.class).getValue());
		assertEquals("abc", tb1.as(Textbox.class).getValue());
		assertEquals("KGB", l1.as(Label.class).getValue());
		
		tb1.type("def");
		assertEquals("", msg1.as(Label.class).getValue());
		assertEquals("def", tb1.as(Textbox.class).getValue());
		assertEquals("def", l1.as(Label.class).getValue());
	}
}
