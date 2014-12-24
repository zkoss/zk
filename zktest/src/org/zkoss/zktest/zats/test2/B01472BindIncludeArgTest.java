package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

public class B01472BindIncludeArgTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent lb1 = desktop.query("#win1 #lb1");
		ComponentAgent lb2 = desktop.query("#win1 #inc1 #win2 #lb2");
		ComponentAgent tb1 = desktop.query("#win1 #tb1");
		ComponentAgent btn1 = desktop.query("#win1 #btn1");
		
		assertEquals("ABC", lb1.as(Label.class).getValue());
		assertEquals("ABC", lb2.as(Label.class).getValue());
		
		tb1.type("XYZ");
		btn1.click();
		lb2 = desktop.query("#win1 #inc1 #win2 #lb2");
		assertEquals("XYZ", lb1.as(Label.class).getValue());
		assertEquals("XYZ", lb2.as(Label.class).getValue());
	}
}
