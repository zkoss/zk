package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01194NestedVMInitTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent headerNameLb = desktop.query("#headerNameLb");
		ComponentAgent vmsNameTxb = desktop.query("#vmsNameTxb");
		ComponentAgent vmsDescTxb = desktop.query("#vmsDescTxb");
		ComponentAgent vmInnerVmDescTxb = desktop.query("#vmInnerVmDescTxb");
		ComponentAgent vmInnerVmDescLb = desktop.query("#vmInnerVmDescLb");
		ComponentAgent outerNameLb = desktop.query("#outerNameLb");
		ComponentAgent outerDescTxb = desktop.query("#outerDescTxb");
		String text = vmsDescTxb.as(Textbox.class).getValue();
		
		assertTrue(text.length() > 0);
		assertEquals(text,  vmInnerVmDescTxb.as(Textbox.class).getValue());
		assertEquals(text,  vmInnerVmDescLb.as(Label.class).getValue());
		assertEquals(text,  outerDescTxb.as(Textbox.class).getValue());

		text = "Ian Tsai 1";
		vmsNameTxb.type(text);
		assertEquals(text,  headerNameLb.as(Label.class).getValue());
		assertEquals(text,  outerNameLb.as(Textbox.class).getValue());
		
		text = "AAA";
		vmsDescTxb.type(text);
		assertEquals(text,  vmInnerVmDescTxb.as(Textbox.class).getValue());
		assertEquals(text,  vmInnerVmDescLb.as(Label.class).getValue());
		assertEquals(text,  outerDescTxb.as(Textbox.class).getValue());
		
		text = "BBB";
		vmInnerVmDescTxb.type(text);
		assertEquals(text,  vmsDescTxb.as(Textbox.class).getValue());
		assertEquals(text,  vmInnerVmDescLb.as(Label.class).getValue());
		assertEquals(text,  outerDescTxb.as(Textbox.class).getValue());
		
		text = "CCC";
		outerDescTxb.type(text);
		assertEquals(text,  vmsDescTxb.as(Textbox.class).getValue());
		assertEquals(text,  vmInnerVmDescLb.as(Label.class).getValue());
		assertEquals(text,  vmInnerVmDescTxb.as(Textbox.class).getValue());
	}
}
