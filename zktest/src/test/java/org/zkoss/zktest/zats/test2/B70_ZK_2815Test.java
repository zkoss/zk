package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Tree;
import org.zkoss.zul.ext.TreeOpenableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class B70_ZK_2815Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> boxes = desktop.queryAll("checkbox");
		boxes.get(2).click();
	}
}
