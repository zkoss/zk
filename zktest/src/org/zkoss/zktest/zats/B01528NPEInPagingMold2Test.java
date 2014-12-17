package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Listcell;

public class B01528NPEInPagingMold2Test extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		
		desktop.query("#btn1").click();
		assertEquals("Item 0 Updated", listbox.queryAll("listitem").get(0).query("listcell").as(Listcell.class).getLabel());
		
		desktop.query("#btn2").click();
		assertEquals("Item 2 Updated", listbox.queryAll("listitem").get(2).query("listcell").as(Listcell.class).getLabel());
		
		desktop.query("#btn3").click();
		assertEquals("Item 5 Updated", listbox.queryAll("listitem").get(5).query("listcell").as(Listcell.class).getLabel());
		
		desktop.query("#btn4").click();
		assertEquals("Item 9 Updated", listbox.queryAll("listitem").get(9).query("listcell").as(Listcell.class).getLabel());
	}
}
