package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01062NullIntValueTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent lb11 = desktop.query("#lb11");
		ComponentAgent lb12 = desktop.query("#lb12");
		ComponentAgent lb21 = desktop.query("#lb21");
		ComponentAgent lb22 = desktop.query("#lb22");
		ComponentAgent msg1 = desktop.query("#msg1");
		ComponentAgent msg2 = desktop.query("#msg2");
		ComponentAgent save = desktop.query("#save");
		
		assertEquals("", lb11.as(Label.class).getValue());
		assertEquals("0", lb12.as(Label.class).getValue());
		assertEquals("", lb21.as(Label.class).getValue());
		assertEquals("0", lb22.as(Label.class).getValue());
		
		save.click();
		assertEquals("", lb11.as(Label.class).getValue());
		assertEquals("0", lb12.as(Label.class).getValue());
		assertEquals("", lb21.as(Label.class).getValue());
		assertEquals("0", lb22.as(Label.class).getValue());
		assertEquals("value1 is null, value2 is 0", msg1.as(Label.class).getValue());
		assertEquals("value1 is null, value2 is 0", msg2.as(Label.class).getValue());
	}
}
