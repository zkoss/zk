package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;

public class B0020Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
        
		//test property init
		assertEquals(5, desktop.queryAll("button").size());
		
		desktop.queryAll("button").get(0).click();
		assertEquals(4, desktop.queryAll("button").size());
		
		desktop.queryAll("button").get(0).click();
		assertEquals(3, desktop.queryAll("button").size());
		
		desktop.queryAll("button").get(0).click();
		assertEquals(2, desktop.queryAll("button").size());
		
		desktop.queryAll("button").get(0).click();
		assertEquals(1, desktop.queryAll("button").size());
		
		desktop.queryAll("button").get(0).click();
		assertEquals(0, desktop.queryAll("button").size());
	}
}
