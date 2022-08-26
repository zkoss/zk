package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B01196BinderUiLcConfusionTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		assertEquals(5, desktop.queryAll("listitem").size());
	}
}
