package org.zkoss.zktest.zats.test2;

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

public class B01017NestedFormPathTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l21 = desktop.query("#l21");
		ComponentAgent l22 = desktop.query("#l22");
		ComponentAgent l31 = desktop.query("#l31");
		ComponentAgent l32 = desktop.query("#l32");
		ComponentAgent t1 = desktop.query("#t1");
		ComponentAgent t2 = desktop.query("#t2");
		ComponentAgent t3 = desktop.query("#t3");
		ComponentAgent msg = desktop.query("#msg");
		ComponentAgent update = desktop.query("#update");
		
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l21.as(Label.class).getValue());
		assertEquals("C", l31.as(Label.class).getValue());
		assertEquals("A", t1.as(Textbox.class).getValue());
		assertEquals("B", t2.as(Textbox.class).getValue());
		assertEquals("C", t3.as(Textbox.class).getValue());
		
		t1.type("Aa");
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("value is 'Aa'", l12.as(Label.class).getValue());
		
		t2.type("Bb");
		assertEquals("B", l21.as(Label.class).getValue());
		assertEquals("value is 'Bb'", l22.as(Label.class).getValue());
		
		t3.type("Cc");
		assertEquals("C", l31.as(Label.class).getValue());
		assertEquals("value is 'Cc'", l32.as(Label.class).getValue());
		
		update.click();
		assertEquals("Aa", l11.as(Label.class).getValue());
		assertEquals("Bb", l21.as(Label.class).getValue());
		assertEquals("Cc", l31.as(Label.class).getValue());
		assertEquals("update value1:Aa,value2:Bb,value3:Cc", msg.as(Label.class).getValue());
	}
}
