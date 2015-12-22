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
import org.zkoss.zul.Listcell;

public class B00892ChildBindingUnderListboxTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent outerbox = desktop.query("#outerbox");
		List<ComponentAgent> items = outerbox.queryAll("listitem");
		assertEquals(4, items.size());
		
		List<ComponentAgent> cell = items.get(0).queryAll("listcell");
		assertEquals(2, cell.size());
		assertEquals("0", cell.get(0).as(Listcell.class).getLabel());
		assertEquals("A", cell.get(1).as(Listcell.class).getLabel());
		
		cell = items.get(1).queryAll("listcell");
		assertEquals(2, cell.size());
		assertEquals("1", cell.get(0).as(Listcell.class).getLabel());
		assertEquals("B", cell.get(1).as(Listcell.class).getLabel());
		
		cell = items.get(2).queryAll("listcell");
		assertEquals(2, cell.size());
		assertEquals("2", cell.get(0).as(Listcell.class).getLabel());
		assertEquals("C", cell.get(1).as(Listcell.class).getLabel());
		
		cell = items.get(3).queryAll("listcell");
		assertEquals(2, cell.size());
		assertEquals("3", cell.get(0).as(Listcell.class).getLabel());
		assertEquals("D", cell.get(1).as(Listcell.class).getLabel());
	}
}
