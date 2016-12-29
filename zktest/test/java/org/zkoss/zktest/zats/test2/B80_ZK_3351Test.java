package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Group;
import org.zkoss.zul.Label;

public class B80_ZK_3351Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("#btn").click();
		assertEquals("done", desktop.query("#lb").as(Label.class).getValue());
	}
}
