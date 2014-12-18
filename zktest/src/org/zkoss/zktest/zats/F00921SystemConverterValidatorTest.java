package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;

public class F00921SystemConverterValidatorTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent l3 = desktop.query("#l3");
		ComponentAgent l4 = desktop.query("#l4");

		assertEquals("XConverterX", l1.as(Label.class).getValue());
		assertEquals("YConverterY", l2.as(Label.class).getValue());
		assertEquals("XValidator", l3.as(Label.class).getValue());
		assertEquals("YValidator", l4.as(Label.class).getValue());
	}
}
