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

public class B00603Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent outsidebox = desktop.query("#outsidebox");
		
		assertEquals(4, outsidebox.getChildren().size());
		String[] itemLabel = {"A", "B", "C"};
		String[] optionLabel = {"A", "B"};
		ComponentAgent outeritem = outsidebox.getChild(0); //don't use queryAll("listitem"), there are many listitems
		for (int i = 0; i < 3; i++) {
			outeritem = outsidebox.getChild(i + 1);
			String outerl = itemLabel[i];
			ComponentAgent cell = outeritem.getChild(0);
			assertEquals(outerl, cell.as(Listcell.class).getLabel());
			
			ComponentAgent innerbox = outeritem.query("listbox");
			assertTrue(innerbox != null);
			
			List<ComponentAgent> inneritems = innerbox.queryAll("listitem");
			assertEquals(2, inneritems.size());
			
			for(int j = 0; j < 2; j++) {
				String innerl = optionLabel[j];
				cell = inneritems.get(j).getChild(0);
				assertEquals(outerl + " " + innerl, cell.as(Listcell.class).getLabel());
				
				cell = inneritems.get(j).getChild(1);
				assertEquals(outerl + " " + innerl + innerl, cell.as(Listcell.class).getLabel());
			}
		}
	}
}
