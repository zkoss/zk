package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

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
