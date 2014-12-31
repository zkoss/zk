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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;

public class B01891ComboboxValidatorTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent cb1 = desktop.query("#cb1");
		ComponentAgent lb11 = desktop.query("#lb11");
		ComponentAgent lb12 = desktop.query("#lb12");
		ComponentAgent cb2 = desktop.query("#cb2");
		ComponentAgent lb21 = desktop.query("#lb21");
		ComponentAgent lb22 = desktop.query("#lb22");
		
		cb1.type("01");
		assertEquals("01", cb1.as(Combobox.class).getText());
		assertEquals("", lb11.as(Label.class).getValue());
		assertEquals("01", lb12.as(Label.class).getValue());
		
		cb1.type("0");
		assertEquals("0", cb1.as(Combobox.class).getText());
		assertEquals("Please select an item!!", lb11.as(Label.class).getValue());
		assertEquals("01", lb12.as(Label.class).getValue());
		
		cb1.type("");
		assertEquals("", cb1.as(Combobox.class).getText());
		assertEquals("Please select an item!!", lb11.as(Label.class).getValue());
		assertEquals("01", lb12.as(Label.class).getValue());
		
		cb2.type("01");
		assertEquals("01", cb2.as(Combobox.class).getText());
		assertEquals("", lb21.as(Label.class).getValue());
		//assertEquals("0", lb22.as(Label.class).getValue());
		
		cb2.type("0");
		assertEquals("0", cb2.as(Combobox.class).getText());
		//assertEquals("Please select an item!!", lb21.as(Label.class).getValue());
		//assertEquals("0", lb22.as(Label.class).getValue());
		
		cb2.type("");
		assertEquals("", cb2.as(Combobox.class).getText());
		//assertEquals("Please select an item!!", lb21.as(Label.class).getValue());
		//assertEquals("0", lb22.as(Label.class).getValue());
	}
}
