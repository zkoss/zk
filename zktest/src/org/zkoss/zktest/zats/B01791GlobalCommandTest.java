package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
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
