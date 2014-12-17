package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00687Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l13 = desktop.query("#l13");
		ComponentAgent l14 = desktop.query("#l14");
		ComponentAgent t11 = desktop.query("#t11");
		ComponentAgent t12 = desktop.query("#t12");
		ComponentAgent t13 = desktop.query("#t13");
		ComponentAgent t14 = desktop.query("#t14");
	
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("C", l13.as(Label.class).getValue());
		assertEquals("D", l14.as(Label.class).getValue());
		
		t11.type("Q");
		assertEquals("Q", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("C", l13.as(Label.class).getValue());
		assertEquals("D", l14.as(Label.class).getValue());
		
		t12.type("W");
		assertEquals("Q", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("C", l13.as(Label.class).getValue());
		assertEquals("D", l14.as(Label.class).getValue());
		
		t13.type("E");
		assertEquals("Q", l11.as(Label.class).getValue());
		assertEquals("W", l12.as(Label.class).getValue());
		assertEquals("E", l13.as(Label.class).getValue());
		assertEquals("D", l14.as(Label.class).getValue());
		
		desktop.query("#btn1").click();
		assertEquals("Q", l11.as(Label.class).getValue());
		assertEquals("W", l12.as(Label.class).getValue());
		assertEquals("E", l13.as(Label.class).getValue());
		assertEquals("command 1", l14.as(Label.class).getValue());
		assertEquals("command 1", t14.as(Textbox.class).getValue());
	}
}
