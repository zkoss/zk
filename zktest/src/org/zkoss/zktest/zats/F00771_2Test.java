package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00771_2Test extends ZATSTestCase {
	
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
		
		t11.type("ab");
		t21.type("de");
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
		
	    ComponentAgent t41 = desktop.query("#t41");
	    ComponentAgent t42 = desktop.query("#t42");
	    ComponentAgent t43 = desktop.query("#t43");
	    ComponentAgent l41 = desktop.query("#l41");
	    ComponentAgent l42 = desktop.query("#l42");
	    ComponentAgent l43 = desktop.query("#l43");
	    ComponentAgent submit = desktop.query("#submit");
	    
	    t41.type("ab");
	    t42.type("de");
	    t43.type("xy");
	    submit.click();
	    assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("value1 must equalsIgnoreCase to abc - by key", l41.as(Label.class).getValue());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l42.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l43.as(Label.class).getValue());
	    
	    t41.type("ABC");
	    submit.click();
	    assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		assertEquals("value2 must equalsIgnoreCase to def - by key", l42.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l43.as(Label.class).getValue());
	    
	    t42.type("DEF");
	    submit.click();
	    assertEquals("", val1.as(Label.class).getValue());
		assertEquals("", val2.as(Label.class).getValue());
		assertEquals("", val3.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		assertEquals("", l42.as(Label.class).getValue());
	    assertEquals("value3 must equalsIgnoreCase to xyz - by key", l43.as(Label.class).getValue());
	    
	    t43.type("XYZ");
	    submit.click();
	    assertEquals("ABC", val1.as(Label.class).getValue());
		assertEquals("DEF", val2.as(Label.class).getValue());
		assertEquals("XYZ", val3.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		assertEquals("", l42.as(Label.class).getValue());
	    assertEquals("", l43.as(Label.class).getValue());
	    assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
	    assertEquals("", l21.as(Label.class).getValue());
	    assertEquals("", l22.as(Label.class).getValue());
	    assertEquals("", l31.as(Label.class).getValue());
	    assertEquals("", l32.as(Label.class).getValue());
	}
}
