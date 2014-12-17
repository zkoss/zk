package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01066IncorrectFormValueTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent save = desktop.query("#save");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");
		
		assertEquals("A", lb1.as(Label.class).getValue());
		assertEquals("A", lb2.as(Label.class).getValue());
		
		tb1.type("Abc");
		assertEquals("A", lb1.as(Label.class).getValue());
		assertEquals("A", lb2.as(Label.class).getValue());
		
		save.click();
		assertEquals("Abc", lb1.as(Label.class).getValue());
		assertEquals("Abc", lb2.as(Label.class).getValue());
	}
}
