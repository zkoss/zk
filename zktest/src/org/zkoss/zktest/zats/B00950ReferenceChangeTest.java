package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00950ReferenceChangeTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent l13 = desktop.query("#l13");
		ComponentAgent l21 = desktop.query("#l21");
		ComponentAgent l22 = desktop.query("#l22");
		ComponentAgent l23 = desktop.query("#l23");
		ComponentAgent clear = desktop.query("#clear");
		
		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
		assertEquals("", l13.as(Label.class).getValue());
		assertEquals("", l21.as(Label.class).getValue());
		assertEquals("", l22.as(Label.class).getValue());
		assertEquals("", l23.as(Label.class).getValue());
		
		listbox.query("listitem").as(SelectAgent.class).select();
		assertEquals("Dennis", l11.as(Label.class).getValue());
		assertEquals("Chen", l12.as(Label.class).getValue());
		assertEquals("Dennis Chen", l13.as(Label.class).getValue());
		assertEquals("Dennis", l21.as(Label.class).getValue());
		assertEquals("Chen", l22.as(Label.class).getValue());
		assertEquals("Dennis Chen", l23.as(Label.class).getValue());
		
		listbox.queryAll("listitem").get(1).as(SelectAgent.class).select();
		assertEquals("Alice", l11.as(Label.class).getValue());
		assertEquals("Lin", l12.as(Label.class).getValue());
		assertEquals("Alice Lin", l13.as(Label.class).getValue());
		assertEquals("Alice", l21.as(Label.class).getValue());
		assertEquals("Lin", l22.as(Label.class).getValue());
		assertEquals("Alice Lin", l23.as(Label.class).getValue());
		
		listbox.queryAll("listitem").get(2).as(SelectAgent.class).select();
		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
		assertEquals("", l13.as(Label.class).getValue());
		assertEquals("", l21.as(Label.class).getValue());
		assertEquals("", l22.as(Label.class).getValue());
		assertEquals("", l23.as(Label.class).getValue());
		
		listbox.queryAll("listitem").get(1).as(SelectAgent.class).select();
		listbox.queryAll("listitem").get(1).query("textbox").type("Grace");
		assertEquals("Grace", l11.as(Label.class).getValue());
		assertEquals("Lin", l12.as(Label.class).getValue());
		assertEquals("Grace Lin", l13.as(Label.class).getValue());
		assertEquals("Grace", l21.as(Label.class).getValue());
		assertEquals("Lin", l22.as(Label.class).getValue());
		assertEquals("Grace Lin", l23.as(Label.class).getValue());
		
		clear.click();
		assertEquals("", l11.as(Label.class).getValue());
		assertEquals("", l12.as(Label.class).getValue());
		assertEquals("", l13.as(Label.class).getValue());
		assertEquals("", l21.as(Label.class).getValue());
		assertEquals("", l22.as(Label.class).getValue());
		assertEquals("", l23.as(Label.class).getValue());
	}
}
