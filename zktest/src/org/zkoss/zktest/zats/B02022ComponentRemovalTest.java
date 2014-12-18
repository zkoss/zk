package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;

public class B02022ComponentRemovalTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent arbtn = desktop.query("#arbtn");
		ComponentAgent btn = desktop.query("#btn");
		ComponentAgent lab = desktop.query("#lab");
		
		assertEquals("0", lab.as(Label.class).getValue());
		arbtn.click();
		assertEquals("1", lab.as(Label.class).getValue());
		btn.click();
		assertEquals("2", lab.as(Label.class).getValue());
	}
}
