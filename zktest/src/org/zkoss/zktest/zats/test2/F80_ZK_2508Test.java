package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class F80_ZK_2508Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent d11 = desktop.query("#d11");
		ComponentAgent d12 = desktop.query("#d12");
		assertEquals(4, d11.getChildren().size());
		assertEquals(4, d12.getChildren().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(d11.getChild(i).getChild(0).as(Label.class).getValue().trim().replaceAll("[\t\n]", ""),
					d12.getChild(i).getChild(0).as(Label.class).getValue().trim());
		}
		
		ComponentAgent d21 = desktop.query("#d21");
		ComponentAgent d22 = desktop.query("#d22");
		assertEquals(2, d21.getChildren().size());
		assertEquals(2, d22.getChildren().size());
		for (int i = 0; i < 2; i++) {
			assertEquals(d21.getChild(i).getChild(0).as(Label.class).getValue().trim().replaceAll("[\t\n]", ""),
					d22.getChild(i).getChild(0).as(Label.class).getValue().trim());
		}
		
		ComponentAgent d31 = desktop.query("#d31");
		ComponentAgent d32 = desktop.query("#d32");
		assertEquals(1, d31.getChildren().size());
		assertEquals(1, d32.getChildren().size());
		for (int i = 0; i < 1; i++) {
			assertEquals(d31.getChild(i).getChild(0).as(Label.class).getValue().trim().replaceAll("[\t\n]", ""),
					d32.getChild(i).getChild(0).as(Label.class).getValue().trim());
		}
	}
}
