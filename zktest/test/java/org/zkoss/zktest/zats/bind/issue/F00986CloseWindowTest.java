package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F00986CloseWindowTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent detach = desktop.query("#detach");
		ComponentAgent win1 = desktop.query("#win1");
		ComponentAgent win2 = desktop.query("#win2");
		ComponentAgent win3 = desktop.query("#win3");
		ComponentAgent win4 = desktop.query("#win4");
		assertTrue(win1 != null);
		assertTrue(win2 != null);
		assertTrue(win3 != null);
		assertFalse(win4 != null);
		
		detach.click();
		win1 = desktop.query("#win1");
		win2 = desktop.query("#win2");
		win3 = desktop.query("#win3");
		win4 = desktop.query("#win4");
		assertFalse(win1 != null);
		assertTrue(win2 != null);
		assertFalse(win3 != null);
		assertFalse(win4 != null);
	}
}
