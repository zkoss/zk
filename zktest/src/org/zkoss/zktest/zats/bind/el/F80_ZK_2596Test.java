package org.zkoss.zktest.zats.bind.el;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F80_ZK_2596Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
			ComponentAgent lb2 = desktop.query("#lb2");
			ComponentAgent lb3 = desktop.query("#lb3");
			ComponentAgent lb4 = desktop.query("#lb4");
			ComponentAgent lb5 = desktop.query("#lb5");
			ComponentAgent lb6 = desktop.query("#lb6");
			ComponentAgent lb7 = desktop.query("#lb7");
			ComponentAgent lb8 = desktop.query("#lb8");
			ComponentAgent lb9 = desktop.query("#lb9");
			ComponentAgent lb10 = desktop.query("#lb10");
			ComponentAgent lb11 = desktop.query("#lb11");
			ComponentAgent lb12 = desktop.query("#lb12");
			ComponentAgent lb13 = desktop.query("#lb13");
			ComponentAgent lb14 = desktop.query("#lb14");
			ComponentAgent btn1 = desktop.query("#btn1");
			ComponentAgent btn2 = desktop.query("#btn2");
			
			assertEquals("4.0", lb2.as(Label.class).getValue());
			assertEquals("4", lb3.as(Label.class).getValue());
			assertEquals("value", lb4.as(Label.class).getValue());
			assertEquals("value6", lb5.as(Label.class).getValue());
			assertEquals("valuehi", lb6.as(Label.class).getValue());
			assertEquals("valuevalue", lb7.as(Label.class).getValue());
			assertEquals("one value two", lb8.as(Label.class).getValue());
			assertEquals("11", lb9.as(Label.class).getValue());
			assertEquals("11", lb10.as(Label.class).getValue());
			assertEquals("21", lb11.as(Label.class).getValue());
			assertEquals("120", lb12.as(Label.class).getValue());
			btn1.click();
			assertEquals("value6", lb13.as(Label.class).getValue());
			btn2.click();
			assertEquals("value611", lb14.as(Label.class).getValue());
		} catch (Exception e) {
			// can't go here
			assertTrue(false);
		}
	}

}
