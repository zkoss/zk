package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F01033InitClassTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent l11 = desktop.query("#win1 #l11");
		ComponentAgent l12 = desktop.query("#win1 #l12");
		ComponentAgent l21 = desktop.query("#win2 #l21");
		ComponentAgent l22 = desktop.query("#win2 #l22");

		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("Chen", l12.as(Label.class).getValue());
		assertEquals("Ian", l21.as(Label.class).getValue());
		assertEquals("Tasi", l22.as(Label.class).getValue());
	}
}
