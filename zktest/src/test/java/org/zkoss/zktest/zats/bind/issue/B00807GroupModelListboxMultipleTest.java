package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;

public class B00807GroupModelListboxMultipleTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		List<ComponentAgent> groups = listbox.queryAll("listgroup");
		List<ComponentAgent> groupfoots = listbox.queryAll("listgroupfoot");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent sel1 = desktop.query("#sel1");
		
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
		
		items.get(3).as(MultipleSelectAgent.class).select();
		assertEquals("[Asparagus]", l1.as(Label.class).getValue());
		
		items.get(4).as(MultipleSelectAgent.class).select();
		assertEquals("[Asparagus, Beets]", l1.as(Label.class).getValue());
		
		sel1.click();
		assertEquals("[Apples, Shrimp]", l1.as(Label.class).getValue());
		
		Set<Listitem> sels = listbox.as(Listbox.class).getSelectedItems();
		ArrayList<Integer> selind = new ArrayList<Integer>();
		for (Listitem i : sels) {
			selind.add(i.getIndex());
		}
		assertEquals("[1, 5]", selind.toString());
	}
}
