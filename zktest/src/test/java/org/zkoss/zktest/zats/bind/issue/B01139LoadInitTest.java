package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01139LoadInitTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent liChk = desktop.query("#liChk");
		ComponentAgent changeNameBtn = desktop.query("#changeNameBtn");
		ComponentAgent nameTexb = desktop.query("#nameTexb");
		ComponentAgent nameLbl = desktop.query("#nameLbl");
		
		nameTexb.type("XYZ");
		changeNameBtn.click();
		assertEquals("XYZ", nameLbl.as(Label.class).getValue());
		
		nameTexb.type("XXX");
		liChk.check(false);
		changeNameBtn.click();
		assertEquals("XYZ", nameLbl.as(Label.class).getValue());
		
		nameTexb.type("XXX");
		liChk.check(true);
		changeNameBtn.click();
		assertEquals("XXX", nameLbl.as(Label.class).getValue());
	}
}
