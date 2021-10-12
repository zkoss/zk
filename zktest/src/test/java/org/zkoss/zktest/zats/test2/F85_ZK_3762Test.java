package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F85_ZK_3762Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("#btn").click();
		assertEquals("done", desktop.query("#lb3762").as(Label.class).getValue());
		assertEquals("done", desktop.query("#lbLabels").as(Label.class).getValue());
	}
}
