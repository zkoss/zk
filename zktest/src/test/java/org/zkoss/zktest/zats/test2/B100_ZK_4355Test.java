package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Timebox;

public class B100_ZK_4355Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		assertNotEquals(5, desktop.query("timebox").as(Timebox.class).getCols());
	}
}
