package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B80_ZK_3211Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent r0 = desktop.query("#result0");
		ComponentAgent r1 = desktop.query("#result1");
		ComponentAgent r2 = desktop.query("#result2");
		ComponentAgent btn0 = desktop.query("#btn0");
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent btn2 = desktop.query("#btn2");
		ComponentAgent move = desktop.query("#move");
		btn0.click();
		assertEquals("my label.", r0.as(Label.class).getValue());
		btn1.click();
		assertEquals("my label1.", r1.as(Label.class).getValue());
		btn2.click();
		assertEquals("my label2.", r2.as(Label.class).getValue());
		move.click();
		btn0.click();
		assertEquals("my label..", r0.as(Label.class).getValue());
		btn1.click();
		assertEquals("my label1..", r1.as(Label.class).getValue());
		btn2.click();
		assertEquals("my label2..", r2.as(Label.class).getValue());
	}
}
