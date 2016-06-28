package org.zkoss.zktest.zats.test2;


import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import static org.junit.Assert.assertTrue;

public class F80_ZK_2955Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("button").click();
		assertTrue(desktop.query("label").as(Label.class).getValue().contains("(recreated)"));
	}
}
