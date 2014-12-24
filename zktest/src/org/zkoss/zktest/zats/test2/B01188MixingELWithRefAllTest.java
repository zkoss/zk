package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Treecell;

public class B01188MixingELWithRefAllTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> items = desktop.query("#halyout1").queryAll("label");
		assertEquals("0.Item 1", items.get(0).as(Label.class).getValue());
		assertEquals("1.Item 2", items.get(1).as(Label.class).getValue());
		
		items = desktop.query("#listbox1").queryAll("listitem");
		assertEquals("0.Item 1", items.get(0).query("listcell > label").as(Label.class).getValue());
		assertEquals("1.Item 2", items.get(1).query("listcell > label").as(Label.class).getValue());
		
		items = desktop.query("#grid1").queryAll("row");
		assertEquals("0.Item 1", items.get(0).query("label").as(Label.class).getValue());
		assertEquals("1.Item 2", items.get(1).query("label").as(Label.class).getValue());
		
		desktop.query("#combobox1").as(OpenAgent.class).open(true);
		items = desktop.query("#combobox1").queryAll("comboitem");
		assertEquals("0.Item 1", items.get(0).as(Comboitem.class).getLabel());
		assertEquals("1.Item 2", items.get(1).as(Comboitem.class).getLabel());
		
		items = desktop.query("#radiogroup1").queryAll("radio");
		assertEquals("0.Item 1", items.get(0).as(Radio.class).getLabel());
		assertEquals("1.Item 2", items.get(1).as(Radio.class).getLabel());
		
		items = desktop.query("#tree1").queryAll("treecell");
		assertEquals("0.Item 1", items.get(0).as(Treecell.class).getLabel());
		assertEquals("0.Item 1-1", items.get(1).as(Treecell.class).getLabel());
		assertEquals("1.Item 1-2", items.get(2).as(Treecell.class).getLabel());
		assertEquals("1.Item 2", items.get(3).as(Treecell.class).getLabel());
	}
}
