package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

public class F0002Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		
		assertEquals("A", tb1.as(Textbox.class).getValue());
		assertEquals("A", l1.as(Label.class).getValue());
		assertEquals("A", tb2.as(Textbox.class).getValue());
		assertEquals("A", l2.as(Label.class).getValue());
	
		tb1.type("XX");
		assertEquals("XX", tb1.as(Textbox.class).getValue());
		assertEquals("XX", l1.as(Label.class).getValue());
		assertEquals("A", tb2.as(Textbox.class).getValue());
		assertEquals("A", l2.as(Label.class).getValue());
		
		tb2.type("YY");
		assertEquals("XX", tb1.as(Textbox.class).getValue());
		assertEquals("XX", l1.as(Label.class).getValue());
		assertEquals("YY", tb2.as(Textbox.class).getValue());
		assertEquals("A", l2.as(Label.class).getValue());
		
		desktop.query("#btn1").click();
		assertEquals("YY", tb1.as(Textbox.class).getValue());
		assertEquals("YY", l1.as(Label.class).getValue());
		assertEquals("YY", tb2.as(Textbox.class).getValue());
		assertEquals("YY", l2.as(Label.class).getValue());
		
		ComponentAgent tb3 = desktop.query("#tb3");
		ComponentAgent l31 = desktop.query("#l31");
		ComponentAgent l32 = desktop.query("#l32");
		assertEquals("B", tb3.as(Textbox.class).getValue());
		assertEquals("B", l31.as(Label.class).getValue());
		assertEquals("B", l32.as(Label.class).getValue());
		
		tb3.type("ZZ");
		assertEquals("ZZ", tb3.as(Textbox.class).getValue());
		assertEquals("B", l31.as(Label.class).getValue());
		assertEquals("B", l32.as(Label.class).getValue());
		
		desktop.query("#btn2").click();
		assertEquals("ZZ", tb3.as(Textbox.class).getValue());
		assertEquals("ZZ", l31.as(Label.class).getValue());
		assertEquals("ZZ", l32.as(Label.class).getValue());
	}
}
