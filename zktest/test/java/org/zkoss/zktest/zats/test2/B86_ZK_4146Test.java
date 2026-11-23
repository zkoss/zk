package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import static org.junit.Assert.assertEquals;

/**
 * @author jameschu
 */
public class B86_ZK_4146Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("button").click();
		ComponentAgent dirtyFlag = desktop.query("window #flag");
		assertEquals("false", dirtyFlag.as(Label.class).getValue());
	}
}
