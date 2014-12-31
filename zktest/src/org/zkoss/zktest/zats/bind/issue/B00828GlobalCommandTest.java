package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B00828GlobalCommandTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent post = desktop.query("#post");
		for (int i = 0; i < 50; i++) {
			post.click();
			List<ComponentAgent> error = desktop.queryAll("window");
			assertTrue(error.size() == 1);
		}
	}
}
