package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

public class F00901ReferenceBindingTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent tb11 = desktop.query("#tb11");
		ComponentAgent tb12 = desktop.query("#tb12");
		ComponentAgent tb13 = desktop.query("#tb13");
		ComponentAgent tb21 = desktop.query("#tb21");
		ComponentAgent tb22 = desktop.query("#tb22");
		ComponentAgent tb23 = desktop.query("#tb23");
		ComponentAgent tb32 = desktop.query("#tb32");
		ComponentAgent tb33 = desktop.query("#tb33");
		ComponentAgent tb43 = desktop.query("#tb43");
		ComponentAgent replace1 = desktop.query("#replace1");
		ComponentAgent replace2 = desktop.query("#replace2");
		
		assertEquals("Dennis", tb11.as(Textbox.class).getValue());
		assertEquals("Dennis", tb21.as(Textbox.class).getValue());
		assertEquals("1234", tb12.as(Textbox.class).getValue());
		assertEquals("1234", tb22.as(Textbox.class).getValue());
		assertEquals("1234", tb32.as(Textbox.class).getValue());
		assertEquals("11 street", tb13.as(Textbox.class).getValue());
		assertEquals("11 street", tb23.as(Textbox.class).getValue());
		assertEquals("11 street", tb33.as(Textbox.class).getValue());
		assertEquals("11 street", tb43.as(Textbox.class).getValue());
		
		tb11.type("Ray");
		assertEquals("Ray", tb11.as(Textbox.class).getValue());
		assertEquals("Ray", tb21.as(Textbox.class).getValue());
		assertEquals("1234", tb12.as(Textbox.class).getValue());
		assertEquals("1234", tb22.as(Textbox.class).getValue());
		assertEquals("1234", tb32.as(Textbox.class).getValue());
		assertEquals("11 street", tb13.as(Textbox.class).getValue());
		assertEquals("11 street", tb23.as(Textbox.class).getValue());
		assertEquals("11 street", tb33.as(Textbox.class).getValue());
		assertEquals("11 street", tb43.as(Textbox.class).getValue());
		
		tb21.type("Bluce");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("1234", tb12.as(Textbox.class).getValue());
		assertEquals("1234", tb22.as(Textbox.class).getValue());
		assertEquals("1234", tb32.as(Textbox.class).getValue());
		assertEquals("11 street", tb13.as(Textbox.class).getValue());
		assertEquals("11 street", tb23.as(Textbox.class).getValue());
		assertEquals("11 street", tb33.as(Textbox.class).getValue());
		assertEquals("11 street", tb43.as(Textbox.class).getValue());
		
		tb12.type("111");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("111", tb12.as(Textbox.class).getValue());
		assertEquals("111", tb22.as(Textbox.class).getValue());
		assertEquals("111", tb32.as(Textbox.class).getValue());
		assertEquals("11 street", tb13.as(Textbox.class).getValue());
		assertEquals("11 street", tb23.as(Textbox.class).getValue());
		assertEquals("11 street", tb33.as(Textbox.class).getValue());
		assertEquals("11 street", tb43.as(Textbox.class).getValue());
		
		tb22.type("222");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("222", tb12.as(Textbox.class).getValue());
		assertEquals("222", tb22.as(Textbox.class).getValue());
		assertEquals("222", tb32.as(Textbox.class).getValue());
		assertEquals("11 street", tb13.as(Textbox.class).getValue());
		assertEquals("11 street", tb23.as(Textbox.class).getValue());
		assertEquals("11 street", tb33.as(Textbox.class).getValue());
		assertEquals("11 street", tb43.as(Textbox.class).getValue());
		
		tb32.type("333");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("333", tb12.as(Textbox.class).getValue());
		assertEquals("333", tb22.as(Textbox.class).getValue());
		assertEquals("333", tb32.as(Textbox.class).getValue());
		assertEquals("11 street", tb13.as(Textbox.class).getValue());
		assertEquals("11 street", tb23.as(Textbox.class).getValue());
		assertEquals("11 street", tb33.as(Textbox.class).getValue());
		assertEquals("11 street", tb43.as(Textbox.class).getValue());
		
		tb13.type("street1");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("333", tb12.as(Textbox.class).getValue());
		assertEquals("333", tb22.as(Textbox.class).getValue());
		assertEquals("333", tb32.as(Textbox.class).getValue());
		assertEquals("street1", tb13.as(Textbox.class).getValue());
		assertEquals("street1", tb23.as(Textbox.class).getValue());
		assertEquals("street1", tb33.as(Textbox.class).getValue());
		assertEquals("street1", tb43.as(Textbox.class).getValue());
		
		tb23.type("street2");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("333", tb12.as(Textbox.class).getValue());
		assertEquals("333", tb22.as(Textbox.class).getValue());
		assertEquals("333", tb32.as(Textbox.class).getValue());
		assertEquals("street2", tb13.as(Textbox.class).getValue());
		assertEquals("street2", tb23.as(Textbox.class).getValue());
		assertEquals("street2", tb33.as(Textbox.class).getValue());
		assertEquals("street2", tb43.as(Textbox.class).getValue());
		
		tb33.type("street3");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("333", tb12.as(Textbox.class).getValue());
		assertEquals("333", tb22.as(Textbox.class).getValue());
		assertEquals("333", tb32.as(Textbox.class).getValue());
		assertEquals("street3", tb13.as(Textbox.class).getValue());
		assertEquals("street3", tb23.as(Textbox.class).getValue());
		assertEquals("street3", tb33.as(Textbox.class).getValue());
		assertEquals("street3", tb43.as(Textbox.class).getValue());
		
		tb43.type("street4");
		assertEquals("Bluce", tb11.as(Textbox.class).getValue());
		assertEquals("Bluce", tb21.as(Textbox.class).getValue());
		assertEquals("333", tb12.as(Textbox.class).getValue());
		assertEquals("333", tb22.as(Textbox.class).getValue());
		assertEquals("333", tb32.as(Textbox.class).getValue());
		assertEquals("street4", tb13.as(Textbox.class).getValue());
		assertEquals("street4", tb23.as(Textbox.class).getValue());
		assertEquals("street4", tb33.as(Textbox.class).getValue());
		assertEquals("street4", tb43.as(Textbox.class).getValue());
		
		replace1.click();
		assertEquals("Alex", tb11.as(Textbox.class).getValue());
		assertEquals("Alex", tb21.as(Textbox.class).getValue());
		assertEquals("888", tb12.as(Textbox.class).getValue());
		assertEquals("888", tb22.as(Textbox.class).getValue());
		assertEquals("888", tb32.as(Textbox.class).getValue());
		assertEquals("888 st", tb13.as(Textbox.class).getValue());
		assertEquals("888 st", tb23.as(Textbox.class).getValue());
		assertEquals("888 st", tb33.as(Textbox.class).getValue());
		assertEquals("888 st", tb43.as(Textbox.class).getValue());
		
		replace2.click();
		assertEquals("Alex", tb11.as(Textbox.class).getValue());
		assertEquals("Alex", tb21.as(Textbox.class).getValue());
		assertEquals("999", tb12.as(Textbox.class).getValue());
		assertEquals("999", tb22.as(Textbox.class).getValue());
		assertEquals("999", tb32.as(Textbox.class).getValue());
		assertEquals("999 st", tb13.as(Textbox.class).getValue());
		assertEquals("999 st", tb23.as(Textbox.class).getValue());
		assertEquals("999 st", tb33.as(Textbox.class).getValue());
		assertEquals("999 st", tb43.as(Textbox.class).getValue());
	}
}
