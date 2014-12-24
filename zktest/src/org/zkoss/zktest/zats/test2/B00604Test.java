package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Listcell;

public class B00604Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent inc1 = desktop.query("#inc1");
		ComponentAgent inc2 = desktop.query("#inc2");
		ComponentAgent listbox1 = inc1.query("listbox");
		ComponentAgent listbox2 = inc2.query("listbox");
		String[] itemLabel1 = {"A", "B", "C"};
		String[] itemLabel2 = {"X", "Y", "Z"};
		
		assertTrue(listbox1 != null);
		assertFalse(listbox2 != null);
		
		ComponentAgent listbox = listbox1;
		for (int i = 0; i < 2; i++) {
			List<ComponentAgent> items = listbox.queryAll("listitem");
			assertEquals(3, items.size());
			for (int j = 0; j < items.size(); j++) {
				ComponentAgent item = items.get(j);
				ComponentAgent cell1 = item.getChild(0);
				ComponentAgent cell2 = item.getChild(1);
				assertEquals(itemLabel1[j], cell1.as(Listcell.class).getLabel());
				assertEquals(itemLabel2[j], cell2.as(Listcell.class).getLabel());
			}
			ComponentAgent btn1 = desktop.query("#btn1");
			btn1.click();
			listbox = inc2.query("listbox");
		}
	}
}
