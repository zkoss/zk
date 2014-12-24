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
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;

public class B00807GroupModelListboxTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		List<ComponentAgent> groups = listbox.queryAll("listgroup");
		List<ComponentAgent> groupfoots = listbox.queryAll("listgroupfoot");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent sel1 = desktop.query("#sel1");
		ComponentAgent sel2 = desktop.query("#sel2");
		
		assertEquals(3, groups.size());
		assertEquals(3, groupfoots.size());
		assertEquals(5, items.size());
		
		assertEquals("Fruits", groups.get(0).as(Listgroup.class).getLabel());
		assertEquals("Seafood", groups.get(1).as(Listgroup.class).getLabel());
		assertEquals("Vegetables", groups.get(2).as(Listgroup.class).getLabel());
		
		assertEquals("1", groupfoots.get(0).query("listcell").as(Listcell.class).getLabel());
		assertEquals("2", groupfoots.get(1).query("listcell").as(Listcell.class).getLabel());
		assertEquals("2", groupfoots.get(2).query("listcell").as(Listcell.class).getLabel());
		
		assertEquals("Apples", items.get(0).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Salmon", items.get(1).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Shrimp", items.get(2).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Asparagus", items.get(3).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Beets", items.get(4).queryAll("label").get(1).as(Label.class).getValue());
		
		items.get(4).as(SelectAgent.class).select();
		assertEquals("Beets", l1.as(Label.class).getValue());
		
		sel1.click();
		assertEquals("Apples", l1.as(Label.class).getValue());
		
		sel2.click();
		assertEquals("Salmon", l1.as(Label.class).getValue());
	}
}
