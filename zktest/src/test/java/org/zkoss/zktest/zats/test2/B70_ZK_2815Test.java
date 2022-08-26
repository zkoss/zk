package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B70_ZK_2815Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> boxes = desktop.queryAll("checkbox");
		boxes.get(2).click();
	}
}
