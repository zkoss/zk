package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

public class B00619Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		assertEquals(1, listbox.as(Listbox.class).getSelectedIndex());

		ComponentAgent tabbox = desktop.query("#tabbox");
		assertEquals(1, tabbox.as(Tabbox.class).getSelectedIndex());
		
		assertFalse(desktop.query("#taba").as(Tab.class).isSelected());
		assertTrue(desktop.query("#tabb").as(Tab.class).isSelected());
		assertFalse(desktop.query("#tabc").as(Tab.class).isSelected());
		
		ComponentAgent itema = desktop.query("#itema");
		itema.as(SelectAgent.class).select();;
		assertEquals(0, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(0, tabbox.as(Tabbox.class).getSelectedIndex());
		assertTrue(desktop.query("#taba").as(Tab.class).isSelected());
		assertFalse(desktop.query("#tabb").as(Tab.class).isSelected());
		assertFalse(desktop.query("#tabc").as(Tab.class).isSelected());
		
		desktop.query("#tabc").as(SelectAgent.class).select();
		assertEquals(2, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(2, tabbox.as(Tabbox.class).getSelectedIndex());
		assertFalse(desktop.query("#taba").as(Tab.class).isSelected());
		assertFalse(desktop.query("#tabb").as(Tab.class).isSelected());
		assertTrue(desktop.query("#tabc").as(Tab.class).isSelected());
	}
}
