package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zhtml.Label;
import org.zkoss.zktest.zats.ZATSTestCase;


public class F80_ZK_2612Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent div = desktop.query("div");
		assertEquals("bbb", div.getChild(0).as(Label.class).getDynamicProperty("textContent"));
	}
}
