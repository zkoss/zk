package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F01044DeferOnChangeTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent tb3 = desktop.query("#tb3");
		ComponentAgent tb4 = desktop.query("#tb4");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent lb3 = desktop.query("#lb3");
		ComponentAgent lb4 = desktop.query("#lb4");
		ComponentAgent lb5 = desktop.query("#lb5");
		ComponentAgent lb6 = desktop.query("#lb6");
		ComponentAgent save2 = desktop.query("#save2");

		assertEquals("B", tb2.as(Textbox.class).getValue());
		assertEquals("B", lb2.as(Label.class).getValue());
		assertEquals("C", tb3.as(Textbox.class).getValue());
		assertEquals("C", lb3.as(Label.class).getValue());
		assertEquals("D", tb4.as(Textbox.class).getValue());
		assertEquals("D", lb4.as(Label.class).getValue());
		assertEquals("C", lb5.as(Label.class).getValue());
		assertEquals("D", lb6.as(Label.class).getValue());
		
		tb2.type("q");
		tb3.type("w");
		tb4.type("e");
		assertEquals("q", tb2.as(Textbox.class).getValue());
		assertEquals("B", lb2.as(Label.class).getValue());
		assertEquals("w", tb3.as(Textbox.class).getValue());
		assertEquals("C", lb3.as(Label.class).getValue());
		assertEquals("e", tb4.as(Textbox.class).getValue());
		assertEquals("D", lb4.as(Label.class).getValue());
		assertEquals("C", lb5.as(Label.class).getValue());
		assertEquals("D", lb6.as(Label.class).getValue());
		
		save2.click();
		assertEquals("q", tb2.as(Textbox.class).getValue());
		assertEquals("q", lb2.as(Label.class).getValue());
		assertEquals("w", tb3.as(Textbox.class).getValue());
		assertEquals("w", lb3.as(Label.class).getValue());
		assertEquals("e", tb4.as(Textbox.class).getValue());
		assertEquals("e", lb4.as(Label.class).getValue());
		assertEquals("w", lb5.as(Label.class).getValue());
		assertEquals("e", lb6.as(Label.class).getValue());
	}
}
