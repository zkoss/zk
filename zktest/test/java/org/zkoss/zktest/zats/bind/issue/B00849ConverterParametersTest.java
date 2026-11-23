package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

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

public class B00849ConverterParametersTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent l21 = desktop.query("#l21");
		ComponentAgent l22 = desktop.query("#l22");
		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent l31 = desktop.query("#l31");
		ComponentAgent l32 = desktop.query("#l32");
		ComponentAgent tb3 = desktop.query("#tb3");
		ComponentAgent cmd1 = desktop.query("#btn1");
		ComponentAgent cmd2 = desktop.query("#btn2");
		ComponentAgent cmd3 = desktop.query("#btn3");
		
		tb1.type("A");
		cmd1.click();
		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
		assertEquals("A:value1", tb1.as(Textbox.class).getValue());
		
		tb2.type("B");
		cmd2.click();
		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
		assertEquals("B:value2", tb2.as(Textbox.class).getValue());
		
		tb3.type("C");
		cmd3.click();
		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
		assertEquals("C:value3", tb3.as(Textbox.class).getValue());
	}
}
