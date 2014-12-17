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

public class F0015Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l13 = desktop.query("#l13");
		ComponentAgent l21 = desktop.query("#l21");
		ComponentAgent l22 = desktop.query("#l22");
		ComponentAgent l23 = desktop.query("#l23");
		
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("C", l13.as(Label.class).getValue());
		assertEquals("", l21.as(Label.class).getValue());
		assertEquals("", l22.as(Label.class).getValue());
		assertEquals("", l23.as(Label.class).getValue());
		
		desktop.query("#btn1").click();
		assertEquals("doCommand1", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("C", l13.as(Label.class).getValue());
		assertEquals("doCommand1", l21.as(Label.class).getValue());
		assertEquals("", l22.as(Label.class).getValue());
		assertEquals("", l23.as(Label.class).getValue());
		
		desktop.query("#btn2").click();
		assertEquals("doCommand1", l11.as(Label.class).getValue());
		assertEquals("doCommand2", l12.as(Label.class).getValue());
		assertEquals("doCommand3", l13.as(Label.class).getValue());
		assertEquals("doCommand1", l21.as(Label.class).getValue());
		assertEquals("doCommand2", l22.as(Label.class).getValue());
		assertEquals("doCommand3", l23.as(Label.class).getValue());
	}
}
