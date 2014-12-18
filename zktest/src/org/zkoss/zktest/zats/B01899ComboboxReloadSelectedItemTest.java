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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;

public class B01899ComboboxReloadSelectedItemTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent cb3 = desktop.query("#w3 #cb3");
		ComponentAgent lb31 = desktop.query("#w3 #lb31");
		ComponentAgent lb32 = desktop.query("#w3 #lb32");
		ComponentAgent cb4 = desktop.query("#w4 #cb4");
		ComponentAgent lb41 = desktop.query("#w4 #lb41");
		ComponentAgent lb42 = desktop.query("#w4 #lb42");
		
		cb3.type("01");
		assertEquals("01", cb3.as(Combobox.class).getText());
		assertEquals("01", lb31.as(Label.class).getValue());
		assertEquals("", lb32.as(Label.class).getValue());
		
		/*cb3.type("0");
		assertEquals("0", cb1.as(Combobox.class).getText());
		assertEquals("Please select an item!!", lb11.as(Label.class).getValue());
		assertEquals("01", lb12.as(Label.class).getValue());*/
		
		/*cb1.type("");
		assertEquals("", cb1.as(Combobox.class).getText());
		assertEquals("Please select an item!!", lb11.as(Label.class).getValue());
		assertEquals("01", lb12.as(Label.class).getValue());*/
		
		cb4.type("01");
		assertEquals("01", cb4.as(Combobox.class).getText());
		assertEquals("01", lb41.as(Label.class).getValue());
		assertEquals("", lb42.as(Label.class).getValue());
		
		/*cb2.type("0");
		assertEquals("0", cb2.as(Combobox.class).getText());
		//assertEquals("Please select an item!!", lb21.as(Label.class).getValue());
		//assertEquals("0", lb22.as(Label.class).getValue());
*/		
		/*cb2.type("");
		assertEquals("", cb2.as(Combobox.class).getText());
		//assertEquals("Please select an item!!", lb21.as(Label.class).getValue());
		//assertEquals("0", lb22.as(Label.class).getValue());
*/	}
}
