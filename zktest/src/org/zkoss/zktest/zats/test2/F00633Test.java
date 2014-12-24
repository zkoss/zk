package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00633Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		assertEquals("onCreate 1", l11.as(Label.class).getValue());
		assertEquals("onCreate 2", l12.as(Label.class).getValue());
		
		desktop.query("#btn1").click();
		assertEquals("doCommand1", l11.as(Label.class).getValue());
		
		desktop.query("#btn2").click();
		assertEquals("doCommand2", l11.as(Label.class).getValue());
		
		desktop.query("#btn3").click();
		assertEquals("doCommand3 btn3 true", l11.as(Label.class).getValue());
		
		desktop.query("#btn4").click();
		assertEquals("doCommand4 3 false null btn4 true", l11.as(Label.class).getValue());
		
		desktop.query("#btn5").click();
		assertEquals("doCommand5 99 true XYZ btn5 true", l11.as(Label.class).getValue());
		
		desktop.query("#btn6").click();
		assertEquals("doCommand6 9 true ABCD btn6 true", l11.as(Label.class).getValue());
		
		desktop.query("#btn7").click();
		assertEquals("doCommandX 9 true XYZ cmd7", l11.as(Label.class).getValue());
		
		desktop.query("#btn8").click();
		assertEquals("doCommandX 22 true ABCD cmd8", l11.as(Label.class).getValue());
		
		desktop.query("#btn9").click();
		assertEquals("doCommandX 9 false EFG cmd9", l11.as(Label.class).getValue());
		
		desktop.query("#btn10").click();
		assertEquals("object is btn10", l12.as(Label.class).getValue());
		
		desktop.query("#btn11").click();
		assertEquals("object is desktop", l12.as(Label.class).getValue());
		
		desktop.query("#btn12").click();
		assertEquals("object is h11", l12.as(Label.class).getValue());
	}
}
