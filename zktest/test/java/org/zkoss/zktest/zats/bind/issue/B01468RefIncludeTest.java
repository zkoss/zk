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
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class B01468RefIncludeTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent lb1 = desktop.query("#win1 #lb1");
		ComponentAgent lb2 = desktop.query("#win1 #lb2");
		ComponentAgent lb3 = desktop.query("#win1 #include #win2 #lb3");
		ComponentAgent lb4 = desktop.query("#win1 #include #win2 #lb4");
		ComponentAgent lb5 = desktop.query("#win1 #include #win3 #lb5");
		ComponentAgent lb6 = desktop.query("#win1 #include #win3 #lb6");
		ComponentAgent tb1 = desktop.query("#win1 #tb1");
		
		assertEquals("ABC", lb1.as(Label.class).getValue());
		assertEquals("ABC", lb2.as(Label.class).getValue());
		assertEquals("ABC", lb3.as(Label.class).getValue());
		assertEquals("ABC", lb4.as(Label.class).getValue());
		assertEquals("ABC", lb5.as(Label.class).getValue());
		assertEquals("ABC", lb6.as(Label.class).getValue());
		
		tb1.type("XYZ");
		assertEquals("XYZ", lb1.as(Label.class).getValue());
		assertEquals("XYZ", lb2.as(Label.class).getValue());
		assertEquals("XYZ", lb3.as(Label.class).getValue());
		assertEquals("XYZ", lb4.as(Label.class).getValue());
		assertEquals("XYZ", lb5.as(Label.class).getValue());
		assertEquals("XYZ", lb6.as(Label.class).getValue());
	}
}
