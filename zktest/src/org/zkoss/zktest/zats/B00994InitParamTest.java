package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

public class B00994InitParamTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#win1 #l1");
		ComponentAgent l2 = desktop.query("#win2 #l2");
		
		assertEquals("foo", l1.as(Label.class).getValue());
		assertEquals("bar", l2.as(Label.class).getValue());
	}
}
