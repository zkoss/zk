package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00638Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l13 = desktop.query("#l13");
		ComponentAgent t11 = desktop.query("#t11");
		ComponentAgent t12 = desktop.query("#t12");
		ComponentAgent l31 = desktop.query("#l31");
		ComponentAgent t31 = desktop.query("#t31");
		ComponentAgent btn1 = desktop.query("#btn1");
		
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("Y2", l13.as(Label.class).getValue());
		
		t11.type("a");
		assertEquals("a", l11.as(Label.class).getValue());
		t12.type("b");
		assertEquals("b", l12.as(Label.class).getValue());
		
		assertEquals("C", l31.as(Label.class).getValue());
		assertEquals("D", t31.as(Textbox.class).getValue());
		btn1.click();
		assertEquals("E", l31.as(Label.class).getValue());
		assertEquals("F", t31.as(Textbox.class).getValue());
	}
}
