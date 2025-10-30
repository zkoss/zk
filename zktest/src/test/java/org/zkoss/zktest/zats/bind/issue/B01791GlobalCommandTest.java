package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01791GlobalCommandTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent lab1 = desktop.query("#lb1");
		ComponentAgent btn = desktop.query("#btn1");
		
		btn.click();
		assertEquals("global: onClick, global", lab1.as(Label.class).getValue());
	}
}
