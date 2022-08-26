package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B00678Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		
		assertEquals("Value A", l1.as(Label.class).getValue());
		assertEquals("msg A", l2.as(Label.class).getValue());
		
		ComponentAgent btn1 = desktop.query("#btn1");
		btn1.click();
		
		assertEquals("Value B", l1.as(Label.class).getValue());
		assertEquals("msg B", l2.as(Label.class).getValue());
		
		ComponentAgent btn2 = desktop.query("#btn2");
		btn2.click();
		
		assertEquals("Value C", l1.as(Label.class).getValue());
		assertEquals("msg C", l2.as(Label.class).getValue());
	}
}
