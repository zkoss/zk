package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01469ScopeParamRefTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#win1 #l1");
		ComponentAgent l2 = desktop.query("#win1 #inc1 #win2 #l2");
		ComponentAgent l3 = desktop.query("#win1 #inc1 #win2 #l3");
		
		assertEquals("ABC", l1.as(Label.class).getValue());
		assertEquals("ABC", l2.as(Label.class).getValue());
		assertEquals("ABC", l3.as(Label.class).getValue());
	}
}
