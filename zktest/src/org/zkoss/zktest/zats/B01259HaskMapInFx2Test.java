package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01259HaskMapInFx2Test extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l13 = desktop.query("#l13");
		ComponentAgent l14 = desktop.query("#l14");
		ComponentAgent l15 = desktop.query("#l15");
				
		assertEquals("Hello World", l11.as(Label.class).getValue());
		assertEquals("Hello World", l12.as(Label.class).getValue());
		assertEquals("Hello World", l13.as(Label.class).getValue());
		assertEquals("Hi Dennis", l14.as(Label.class).getValue());
		assertEquals("Hi Dennis", l15.as(Label.class).getValue());    
	}
}
