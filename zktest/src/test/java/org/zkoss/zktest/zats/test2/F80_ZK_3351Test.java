package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F80_ZK_3351Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("#btn").click();
		assertEquals("done", desktop.query("#lb").as(Label.class).getValue());
	}
}
