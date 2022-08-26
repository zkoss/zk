package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

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
