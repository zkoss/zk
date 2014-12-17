package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F01046PublicEventTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent msg1 = desktop.query("#win1 #msg1");
		ComponentAgent btn1 = desktop.query("#win1 #btn1");
		ComponentAgent msg2 = desktop.query("#win2 #msg2");
		ComponentAgent btn2 = desktop.query("#win2 #btn2");

		btn1.click();
		assertEquals("Hello i am a vm", msg2.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("Hello i am a composer", msg1.as(Label.class).getValue());
	}
}
