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

public class F0013Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent t1 = desktop.query("#t1");
		ComponentAgent t2 = desktop.query("#t2");
		
		assertEquals("A", l1.as(Label.class).getValue());
		assertEquals("B", l2.as(Label.class).getValue());
		assertEquals("A", t1.as(Textbox.class).getValue());
		assertEquals("B", t2.as(Textbox.class).getValue());
		
		t1.type("Dennis");
		t2.type("Chen");
		desktop.query("#btn1").click();;
		assertEquals("Dennis-cmd1", l1.as(Label.class).getValue());
		assertEquals("Chen-cmd1", l2.as(Label.class).getValue());
		assertEquals("Dennis", t1.as(Textbox.class).getValue());
		assertEquals("Chen", t2.as(Textbox.class).getValue());
		
		t1.type("Alice");
		t2.type("Wu");
		desktop.query("#btn2").click();
		assertEquals("Alice-cmd2", l1.as(Label.class).getValue());
		assertEquals("Wu-cmd2", l2.as(Label.class).getValue());
		assertEquals("Alice-cmd2", t1.as(Textbox.class).getValue());
		assertEquals("Wu-cmd2", t2.as(Textbox.class).getValue());
		
		t1.type("Jumper");
		t2.type("Tj");
		desktop.query("#btn3").click();
		assertEquals("Jumper-cmd3", l1.as(Label.class).getValue());
		assertEquals("Tj-cmd3", l2.as(Label.class).getValue());
		assertEquals("Jumper-cmd3", t1.as(Textbox.class).getValue());
		assertEquals("Tj-cmd3", t2.as(Textbox.class).getValue());
	}
}
