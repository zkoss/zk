package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00878WrongValueException2Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent focus = desktop.query("button");
		//bandbox
		ComponentAgent l = desktop.query("#l1");
		ComponentAgent inp = desktop.query("#inp1");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("A");
		focus.click();
		assertEquals("A", l.as(Label.class).getValue());
		//should check error message
		inp.type("");
		focus.click();
		assertEquals("A", l.as(Label.class).getValue());
		//should check error message
		inp.type("B");
		focus.click();
		assertEquals("B", l.as(Label.class).getValue());
		//should check error message
		
		//combobox
		l = desktop.query("#l2");
		inp = desktop.query("#inp2");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("A");
		focus.click();
		assertEquals("A", l.as(Label.class).getValue());
		//should check error message
		inp.type("");
		focus.click();
		assertEquals("A", l.as(Label.class).getValue());
		//should check error message
		inp.type("B");
		focus.click();
		assertEquals("B", l.as(Label.class).getValue());
		//should check error message
		
		//textbox
		l = desktop.query("#l10");
		inp = desktop.query("#inp10");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("A");
		focus.click();
		assertEquals("A", l.as(Label.class).getValue());
		//should check error message
		inp.type("");
		focus.click();
		assertEquals("A", l.as(Label.class).getValue());
		//should check error message
		inp.type("B");
		focus.click();
		assertEquals("B", l.as(Label.class).getValue());
		//should check error message
		
		//decimalbox
		l = desktop.query("#l4");
		inp = desktop.query("#inp4");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("1");
		focus.click();
		assertEquals("1.0", l.as(Label.class).getValue());
		//should check error message
		inp.type("-1");
		focus.click();
		assertEquals("1.0", l.as(Label.class).getValue());
		//should check error message
		inp.type("2");
		focus.click();
		assertEquals("2.0", l.as(Label.class).getValue());
		//should check error message
		
		//doublebox
		l = desktop.query("#l5");
		inp = desktop.query("#inp5");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("1");
		focus.click();
		assertEquals("1.0", l.as(Label.class).getValue());
		//should check error message
		inp.type("-1");
		focus.click();
		assertEquals("1.0", l.as(Label.class).getValue());
		//should check error message
		inp.type("2");
		focus.click();
		assertEquals("2.0", l.as(Label.class).getValue());
		//should check error message
		
		//doublespinner
		l = desktop.query("#l6");
		inp = desktop.query("#inp6");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("1");
		focus.click();
		assertEquals("1.0", l.as(Label.class).getValue());
		//should check error message
		inp.type("-1");
		focus.click();
		assertEquals("1.0", l.as(Label.class).getValue());
		//should check error message
		inp.type("2");
		focus.click();
		assertEquals("2.0", l.as(Label.class).getValue());
		//should check error message

		//intbox
		l = desktop.query("#l7");
		inp = desktop.query("#inp7");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("1");
		focus.click();
		assertEquals("1", l.as(Label.class).getValue());
		//should check error message
		inp.type("-1");
		focus.click();
		assertEquals("1", l.as(Label.class).getValue());
		//should check error message
		inp.type("2");
		focus.click();
		assertEquals("2", l.as(Label.class).getValue());
		//should check error message
		
		//longbox
		l = desktop.query("#l8");
		inp = desktop.query("#inp8");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("1");
		focus.click();
		assertEquals("1", l.as(Label.class).getValue());
		//should check error message
		inp.type("-1");
		focus.click();
		assertEquals("1", l.as(Label.class).getValue());
		//should check error message
		inp.type("2");
		focus.click();
		assertEquals("2", l.as(Label.class).getValue());
		//should check error message
		
		//spinner
		l = desktop.query("#l9");
		inp = desktop.query("#inp9");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("1");
		focus.click();
		assertEquals("1", l.as(Label.class).getValue());
		//should check error message
		inp.type("-1");
		focus.click();
		assertEquals("1", l.as(Label.class).getValue());
		//should check error message
		inp.type("2");
		focus.click();
		assertEquals("2", l.as(Label.class).getValue());
		//should check error message
		
		//datebox
		l = desktop.query("#l3");
		inp = desktop.query("#inp3");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("20120223");
		focus.click();
		assertEquals("20120223", l.as(Label.class).getValue());
		//should check error message
		inp.type("20110101");
		focus.click();
		assertEquals("20120223", l.as(Label.class).getValue());
		//should check error message
		inp.type("20120223");
		focus.click();
		assertEquals("20120223", l.as(Label.class).getValue());
		//should check error message
		
		//timebox
		l = desktop.query("#l11");
		inp = desktop.query("#inp11");
		assertEquals("", l.as(Label.class).getValue());
		inp.type("13:00");
		focus.click();
		assertEquals("13:00", l.as(Label.class).getValue());
		//should check error message
		inp.type("10:00");
		focus.click();
		assertEquals("13:00", l.as(Label.class).getValue());
		//should check error message
		inp.type("14:00");
		focus.click();
		assertEquals("14:00", l.as(Label.class).getValue());
		//should check error message
		
		/*
	      var errorPopup = jq(".z-errorbox")
	      verifyEquals(0, errorPopup.length())
	      verifyEquals(1, errorPopup.length())
	      verifyEquals(0, errorPopup.length())
        */
	}
}
