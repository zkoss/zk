package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00771_1Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect("/F00771.zul");
		
		ComponentAgent val1 = desktop.query("#val1");
		ComponentAgent val2 = desktop.query("#val2");
		ComponentAgent val3 = desktop.query("#val3");
		ComponentAgent t11 = desktop.query("#t11");
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent t21 = desktop.query("#t21");
		ComponentAgent l21 = desktop.query("#l21");
		ComponentAgent l22 = desktop.query("#l22");
		ComponentAgent t31 = desktop.query("#t31");
		ComponentAgent l31 = desktop.query("#l31");
		ComponentAgent l32 = desktop.query("#l32");
		ComponentAgent reload1 = desktop.query("#reload1");
		ComponentAgent reload2 = desktop.query("#reload2");
		
		assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		
		t11.type("ab");
		t11.type("ab");
		assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.as(Label.class).getValue());
	    assertEquals("", l21.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.as(Label.class).getValue());
	    assertEquals("", l31.as(Label.class).getValue());
	    assertEquals("", l32.as(Label.class).getValue());
	    
	    t21.type("de");
		assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def", l21.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.as(Label.class).getValue());
	    assertEquals("", l31.as(Label.class).getValue());
	    assertEquals("", l32.as(Label.class).getValue());
	    
	    t31.type("xy");
		assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def", l21.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz", l31.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.as(Label.class).getValue());
	    
	    t11.type("abc");
		assertEquals("abc", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("", l11.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def - by key", l12.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def", l21.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def - by key", l22.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz", l31.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.as(Label.class).getValue());
	    
	    t21.type("def");
		assertEquals("abc", val1.as(Label.class).getValue());
		assertEquals("def", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("", l11.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l12.as(Label.class).getValue());
	    assertEquals("", l21.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l22.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz", l31.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.as(Label.class).getValue());
	    
	    t31.type("xyz");
		assertEquals("abc", val1.as(Label.class).getValue());
		assertEquals("def", val2.as(Label.class).getValue());
		assertEquals("xyz", val3.as(Label.class).getValue());
		assertEquals("", l11.as(Label.class).getValue());
	    assertEquals("", l12.as(Label.class).getValue());
	    assertEquals("", l21.as(Label.class).getValue());
	    assertEquals("", l22.as(Label.class).getValue());
	    assertEquals("", l31.as(Label.class).getValue());
	    assertEquals("", l32.as(Label.class).getValue());
	    
	    t11.type("ab");
	    t21.type("de");
	    t31.type("xy");
	    assertEquals("abc", val1.as(Label.class).getValue());
		assertEquals("def", val2.as(Label.class).getValue());
		assertEquals("xyz", val3.as(Label.class).getValue());
		assertEquals("value1 must equalsIgnoreCase to abc", l11.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l12.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def", l21.as(Label.class).getValue());
	    assertEquals("value1 must equalsIgnoreCase to abc - by key", l22.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz", l31.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.as(Label.class).getValue());
	    
	    reload1.click();
	    assertEquals("abc", t11.as(Textbox.class).getValue());
		assertEquals("de", t21.as(Textbox.class).getValue());
		assertEquals("xy", t31.as(Textbox.class).getValue());
		assertEquals("", l11.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def - by key", l12.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def", l21.as(Label.class).getValue());
	    assertEquals("value2 must equalsIgnoreCase to def - by key", l22.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz", l31.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.as(Label.class).getValue());
	    
	    reload2.click();
	    assertEquals("abc", t11.as(Textbox.class).getValue());
		assertEquals("def", t21.as(Textbox.class).getValue());
		assertEquals("xy", t31.as(Textbox.class).getValue());
		assertEquals("", l11.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l12.as(Label.class).getValue());
	    assertEquals("", l21.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l22.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz", l31.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l32.as(Label.class).getValue());
	}
}
