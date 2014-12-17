package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01259HaskMapInFx1Test extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l13 = desktop.query("#l13");
		ComponentAgent l14 = desktop.query("#l14");
		ComponentAgent l15 = desktop.query("#l15");
		ComponentAgent t21 = desktop.query("#t21");
		ComponentAgent t22 = desktop.query("#t22");
		ComponentAgent btn2 = desktop.query("#btn2");
		ComponentAgent t31 = desktop.query("#t31");
		ComponentAgent t32 = desktop.query("#t32");
		ComponentAgent btn3 = desktop.query("#btn3");
		
		assertEquals("Hello World", l11.as(Label.class).getValue());
		assertEquals("Hello World", l12.as(Label.class).getValue());
		assertEquals("Hello World", l13.as(Label.class).getValue());
		assertEquals("Hi Dennis", l14.as(Label.class).getValue());
		assertEquals("Hi Dennis", l15.as(Label.class).getValue());
		
		t21.type("AAA");
		t22.type("BBB");
		assertEquals("Hello World", l11.as(Label.class).getValue());
		assertEquals("Hello World", l12.as(Label.class).getValue());
		assertEquals("Hello World", l13.as(Label.class).getValue());
		assertEquals("Hi Dennis", l14.as(Label.class).getValue());
		assertEquals("Hi Dennis", l15.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("AAA", l11.as(Label.class).getValue());
		assertEquals("AAA", l12.as(Label.class).getValue());
		assertEquals("AAA", l13.as(Label.class).getValue());
		assertEquals("BBB", l14.as(Label.class).getValue());
		assertEquals("BBB", l15.as(Label.class).getValue());
		
		t31.type("CCC");
		t32.type("DDD");
		assertEquals("AAA", l11.as(Label.class).getValue());
		assertEquals("CCC", l12.as(Label.class).getValue());
		assertEquals("AAA", l13.as(Label.class).getValue());
		assertEquals("BBB", l14.as(Label.class).getValue());
		assertEquals("DDD", l15.as(Label.class).getValue());
		
		btn3.click();
		assertEquals("CCC", l11.as(Label.class).getValue());
		assertEquals("CCC", l12.as(Label.class).getValue());
		assertEquals("CCC", l13.as(Label.class).getValue());
		assertEquals("DDD", l14.as(Label.class).getValue());
		assertEquals("DDD", l15.as(Label.class).getValue());
	}
}
