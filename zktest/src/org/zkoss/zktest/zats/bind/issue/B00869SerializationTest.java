package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

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
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Textbox;

public class B00869SerializationTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent msg = desktop.query("#msg");
		ComponentAgent selected = desktop.query("#win #selected");
		ComponentAgent listbox = desktop.query("#win #listbox");
		ComponentAgent tb1 = desktop.query("#win #tb1");
		ComponentAgent save = desktop.query("#win #save");
		ComponentAgent serialize = desktop.query("#serialize");
		ComponentAgent children = desktop.query("#win #children");
		
		assertEquals("A", selected.as(Label.class).getValue());
		assertEquals("A", tb1.as(Textbox.class).getValue());
		assertEquals("B", children.queryAll("label").get(1).as(Label.class).getValue());
		
		List<ComponentAgent> items = listbox.queryAll("listitem");
		items.get(1).select();
		assertEquals("B", selected.as(Label.class).getValue());
		assertEquals("B", tb1.as(Textbox.class).getValue());
		
		tb1.type("BX");
		save.click();
		assertEquals("BX", selected.as(Label.class).getValue());
		assertEquals("BX", children.queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("BX", items.get(1).queryAll("listcell").get(1).as(Listcell.class).getLabel());
		
		serialize.click();
		msg = desktop.query("#msg");
		selected = desktop.query("#win #selected");
		listbox = desktop.query("#win #listbox");
		tb1 = desktop.query("#win #tb1");
		save = desktop.query("#win #save");
		serialize = desktop.query("#serialize");
		children = desktop.query("#win #children");
		items = listbox.queryAll("listitem");
		assertTrue(msg.as(Label.class).getValue().startsWith("done deserialize:"));
		
		items.get(2).select();
		assertEquals("C", selected.as(Label.class).getValue());
		assertEquals("C", tb1.as(Textbox.class).getValue());
		
		tb1.type("CY");
		save.click();
		assertEquals("CY", selected.as(Label.class).getValue());
		assertEquals("CY", children.queryAll("label").get(2).as(Label.class).getValue());
		assertEquals("CY", items.get(2).queryAll("listcell").get(1).as(Listcell.class).getLabel());
	}
}
