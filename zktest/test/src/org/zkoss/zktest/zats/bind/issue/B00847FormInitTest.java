package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B00847FormInitTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent cmd1 = desktop.query("#cmd1");
		ComponentAgent cmd2 = desktop.query("#cmd2");
		
		assertEquals("blue", l1.as(Label.class).getValue());
		assertEquals("blue", l2.as(Label.class).getValue());
		
		cmd1.click();
		assertEquals("red", l1.as(Label.class).getValue());
		assertEquals("blue", l2.as(Label.class).getValue());
		
		cmd2.click();
		assertEquals("yellow", l1.as(Label.class).getValue());
		assertEquals("yellow", l2.as(Label.class).getValue());
	}
}
