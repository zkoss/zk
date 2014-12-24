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
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class B01060DirectModelTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox1 = desktop.query("#listbox1");
		ComponentAgent listbox2 = desktop.query("#listbox2");
		ComponentAgent listbox3 = desktop.query("#listbox3");
		List<ComponentAgent> items1 = listbox1.queryAll("listitem");
		List<ComponentAgent> items2 = listbox2.queryAll("listitem");
		List<ComponentAgent> items3 = listbox3.queryAll("listitem");
		
		String[] labels = {"A", "B", "C"};
		assertEquals(3, items1.size());
		for (int i = 0; i < items1.size(); i++) {
			assertEquals(labels[i], items1.get(i).as(Listitem.class).getLabel());
		}
		assertEquals(3, items2.size());
		for (int i = 0; i < items2.size(); i++) {
			assertEquals("", items2.get(i).as(Listitem.class).getLabel());
		}
		assertEquals(3, items3.size());
		for (int i = 0; i < items3.size(); i++) {
			assertEquals(labels[i], items3.get(i).as(Listitem.class).getLabel());
		}
	}
}
