package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01088FormUpdateTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent lb11 = desktop.query("#lb11");
		ComponentAgent lb12 = desktop.query("#lb12");
		ComponentAgent tb11 = desktop.query("#tb11");
		ComponentAgent tb12 = desktop.query("#tb12");
		ComponentAgent lb21 = desktop.query("#lb21");
		ComponentAgent lb22 = desktop.query("#lb22");
		ComponentAgent reload = desktop.query("#reload");
		ComponentAgent save = desktop.query("#save");
		

		assertEquals("Dennis", lb11.as(Label.class).getValue());
		assertEquals("Chen", lb12.as(Label.class).getValue());
		assertEquals("Dennis", tb11.as(Textbox.class).getValue());
		assertEquals("Chen", tb12.as(Textbox.class).getValue());
		assertEquals("Dennis Chen", lb21.as(Label.class).getValue());
		assertEquals("false", lb22.as(Label.class).getValue());
		
		tb11.type("chunfu");
		tb12.type("chang");
		assertEquals("Dennis", lb11.as(Label.class).getValue());
		assertEquals("Chen", lb12.as(Label.class).getValue());
		assertEquals("chunfu", tb11.as(Textbox.class).getValue());
		assertEquals("chang", tb12.as(Textbox.class).getValue());
		assertEquals("Dennis Chen", lb21.as(Label.class).getValue());
		assertEquals("true", lb22.as(Label.class).getValue());
		
		reload.click();
		assertEquals("Dennis", lb11.as(Label.class).getValue());
		assertEquals("Chen", lb12.as(Label.class).getValue());
		assertEquals("Dennis", tb11.as(Textbox.class).getValue());
		assertEquals("Chen", tb12.as(Textbox.class).getValue());
		assertEquals("Dennis Chen", lb21.as(Label.class).getValue());
		assertEquals("false", lb22.as(Label.class).getValue());
		
		tb11.type("chunfu");
		tb12.type("chang");
		save.click();
		assertEquals("chunfu", lb11.as(Label.class).getValue());
		assertEquals("chang", lb12.as(Label.class).getValue());
		assertEquals("chunfu", tb11.as(Textbox.class).getValue());
		assertEquals("chang", tb12.as(Textbox.class).getValue());
		assertEquals("chunfu chang", lb21.as(Label.class).getValue());
		assertEquals("false", lb22.as(Label.class).getValue());
		
		
	}
}
