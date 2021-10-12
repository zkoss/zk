package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B65_ZK_1944Test extends ZATSTestCase {
	@Test public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent d1 = desktop.query("#d1");
		ComponentAgent d2 = desktop.query("#d2");
		assertEquals("12.3", d1.as(Decimalbox.class).getValue().toString());
		assertEquals("12.3", d2.as(Decimalbox.class).getValue().toString());
	}
}