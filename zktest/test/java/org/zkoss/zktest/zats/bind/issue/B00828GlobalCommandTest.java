package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B00828GlobalCommandTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent post = desktop.query("#post");
		ComponentAgent msg = desktop.query("#msg");
		try {
			for (int i = 0; i < 50; i++) {
				post.click();
				assertEquals(String.valueOf(i + 1), msg.as(Label.class).getValue());
			}
		} catch (Exception e) {
			// should never come here
			assertTrue(false);
		}
	}
}
