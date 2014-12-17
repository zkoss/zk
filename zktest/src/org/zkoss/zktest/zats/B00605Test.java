package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Textbox;

public class B00605Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent tb2 = desktop.query("#inc1").query("textbox"); //can't use #tb2???
		ComponentAgent lb2 = desktop.query("#inc1").query("label"); //can't use #lb2???
		assertEquals("A", tb1.as(Textbox.class).getValue());
		assertEquals("A", lb1.as(Label.class).getValue());
		assertEquals("A", tb2.as(Textbox.class).getValue()); 
		assertEquals("A", lb2.as(Label.class).getValue()); 
		assertTrue(desktop.query("inc2") == null);
		
		tb1.type("JJ");
		assertEquals("JJ", tb1.as(Textbox.class).getValue());
	    assertEquals("JJ", lb1.as(Label.class).getValue());
	    assertEquals("A", tb2.as(Textbox.class).getValue()); 
		assertEquals("A", lb2.as(Label.class).getValue());
		
		tb2.type("KK");
		assertEquals("JJ", tb1.as(Textbox.class).getValue());
	    assertEquals("JJ", lb1.as(Label.class).getValue());
	    assertEquals("KK", tb2.as(Textbox.class).getValue()); 
		assertEquals("KK", lb2.as(Label.class).getValue());
		
		ComponentAgent btn1 = desktop.query("#btn1");
		btn1.click();
		ComponentAgent tb3 = desktop.query("#inc2").query("textbox"); //can't use #tb2???
		ComponentAgent lb3 = desktop.query("#inc2").query("label"); //can't use #lb2???
		assertEquals("A", tb3.as(Textbox.class).getValue()); 
		assertEquals("A", lb3.as(Label.class).getValue());
		
		tb3.type("LL");
		assertEquals("JJ", tb1.as(Textbox.class).getValue());
	    assertEquals("JJ", lb1.as(Label.class).getValue());
	    assertEquals("KK", tb2.as(Textbox.class).getValue()); 
		assertEquals("KK", lb2.as(Label.class).getValue());
		assertEquals("LL", tb3.as(Textbox.class).getValue()); 
		assertEquals("LL", lb3.as(Label.class).getValue());
		
		//test again since inc2 appears
		tb1.type("X");
		assertEquals("X", tb1.as(Textbox.class).getValue());
	    assertEquals("X", lb1.as(Label.class).getValue());
	    assertEquals("KK", tb2.as(Textbox.class).getValue()); 
		assertEquals("KK", lb2.as(Label.class).getValue());
		assertEquals("LL", tb3.as(Textbox.class).getValue()); 
		assertEquals("LL", lb3.as(Label.class).getValue());
		
		tb2.type("Y");
		assertEquals("X", tb1.as(Textbox.class).getValue());
	    assertEquals("X", lb1.as(Label.class).getValue());
	    assertEquals("Y", tb2.as(Textbox.class).getValue()); 
		assertEquals("Y", lb2.as(Label.class).getValue());
		assertEquals("LL", tb3.as(Textbox.class).getValue()); 
		assertEquals("LL", lb3.as(Label.class).getValue());
		
		tb3.type("Z");
		assertEquals("X", tb1.as(Textbox.class).getValue());
	    assertEquals("X", lb1.as(Label.class).getValue());
	    assertEquals("Y", tb2.as(Textbox.class).getValue()); 
		assertEquals("Y", lb2.as(Label.class).getValue());
		assertEquals("Z", tb3.as(Textbox.class).getValue()); 
		assertEquals("Z", lb3.as(Label.class).getValue());
	}
}
