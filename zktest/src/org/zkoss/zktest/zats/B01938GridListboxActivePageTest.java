package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;

public class B01938GridListboxActivePageTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent intbox = desktop.query("#intbox");
		ComponentAgent grid = desktop.query("#grid");
		ComponentAgent listbox = desktop.query("#listbox");
		
		intbox.type("23");
		assertEquals(23, grid.as(Grid.class).getActivePage());
		assertEquals(23, listbox.as(Listbox.class).getActivePage());
		
		intbox.type("123");
		assertEquals(123, grid.as(Grid.class).getActivePage());
		assertEquals(123, listbox.as(Listbox.class).getActivePage());
	}
}
