package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class F01231AfterComposeVMTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent myWin = desktop.query("#myWin");
		ComponentAgent headerLb = desktop.query("#headerLb");
		ComponentAgent nameLb = desktop.query("#nameLb");
		ComponentAgent descTxb = desktop.query("#descTxb");
		
		assertEquals("AAAA", myWin.as(Window.class).getTitle());
		assertEquals("This is a label", headerLb.as(Label.class).getValue());
		assertEquals("admin", nameLb.as(Textbox.class).getValue());
		assertEquals("this is desc", descTxb.as(Textbox.class).getValue());
	}
}
