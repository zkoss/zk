package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B80_ZK_2869Test extends ZATSTestCase {
	@Test public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent btn1 = desktop.query("#trigger");
		btn1.click();
		ComponentAgent btn2 = desktop.query("#trigger2");
		btn2.click();
		assertEquals("CONTENT", desktop.query("#d1").getFirstChild().getFirstChild().query("label").as(Label.class).getValue().trim());
		assertEquals("CONTENT", desktop.query("#d2").getFirstChild().getFirstChild().query("label").as(Label.class).getValue().trim());
	}
}