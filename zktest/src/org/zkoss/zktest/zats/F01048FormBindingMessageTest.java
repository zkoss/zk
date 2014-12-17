package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F01048FormBindingMessageTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent tb3 = desktop.query("#tb3");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent lb3 = desktop.query("#lb3");
		ComponentAgent save = desktop.query("#save");
		
		save.click();
		assertEquals("First name is missing.", lb1.as(Label.class).getValue());
		assertEquals("Last name is missing.", lb2.as(Label.class).getValue());
		assertEquals("Age is missing.", lb3.as(Label.class).getValue());
		
		tb1.type("Dennis");
		assertEquals("", lb1.as(Label.class).getValue());
		
		tb2.type("Chen");
		assertEquals("", lb2.as(Label.class).getValue());
		
		tb3.type("35");
		assertEquals("", lb3.as(Label.class).getValue());
		
		tb1.type("");
		tb3.type("");
		assertEquals("", lb1.as(Label.class).getValue());
		assertEquals("", lb2.as(Label.class).getValue());
		assertEquals("", lb3.as(Label.class).getValue());
		
		save.click();
		assertEquals("First name is missing.", lb1.as(Label.class).getValue());
		assertEquals("", lb2.as(Label.class).getValue());
		assertEquals("Age is missing.", lb3.as(Label.class).getValue());
		
		tb1.type("DennisA");
		assertEquals("", lb1.as(Label.class).getValue());
		
		tb2.type("ChenB");
		assertEquals("", lb2.as(Label.class).getValue());
		
		tb3.type("37");
		assertEquals("", lb3.as(Label.class).getValue());

		save.click();
		assertEquals("", lb1.as(Label.class).getValue());
		assertEquals("", lb2.as(Label.class).getValue());
		assertEquals("", lb3.as(Label.class).getValue());
		assertEquals("Update DennisA,ChenB,37", desktop.query("#msg").as(Label.class).getValue());
	}
}
