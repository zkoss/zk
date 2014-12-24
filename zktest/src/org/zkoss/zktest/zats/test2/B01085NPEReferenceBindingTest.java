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
import org.zkoss.zul.Listbox;

public class B01085NPEReferenceBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox1 = desktop.query("#listbox1");
		ComponentAgent listbox2 = desktop.query("#listbox2");
		ComponentAgent listbox3 = desktop.query("#listbox3");
		ComponentAgent listbox4 = desktop.query("#listbox4");
		ComponentAgent listbox5 = desktop.query("#listbox5");
		ComponentAgent listbox6 = desktop.query("#listbox6");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");
		
		assertEquals(-1, listbox1.as(Listbox.class).getSelectedIndex());
		assertEquals(-1, listbox2.as(Listbox.class).getSelectedIndex());
		assertEquals(-1, listbox3.as(Listbox.class).getSelectedIndex());
		assertEquals("", lb1.as(Label.class).getValue());
		assertEquals(1, listbox4.as(Listbox.class).getSelectedIndex());
		assertEquals(1, listbox5.as(Listbox.class).getSelectedIndex());
		assertEquals(1, listbox6.as(Listbox.class).getSelectedIndex());
		assertEquals("1", lb2.as(Label.class).getValue());
		
		listbox1.query("listitem").as(SelectAgent.class).select();
		assertEquals(0, listbox2.as(Listbox.class).getSelectedIndex());
		assertEquals(0, listbox3.as(Listbox.class).getSelectedIndex());
		assertEquals("0", lb1.as(Label.class).getValue());
		
		listbox2.queryAll("listitem").get(1).as(SelectAgent.class).select();
		assertEquals(1, listbox1.as(Listbox.class).getSelectedIndex());
		assertEquals(1, listbox3.as(Listbox.class).getSelectedIndex());
		assertEquals("1", lb1.as(Label.class).getValue());
		
		listbox3.queryAll("listitem").get(2).as(SelectAgent.class).select();
		assertEquals(2, listbox1.as(Listbox.class).getSelectedIndex());
		assertEquals(2, listbox2.as(Listbox.class).getSelectedIndex());
		assertEquals("2", lb1.as(Label.class).getValue());
		
		listbox4.queryAll("listitem").get(0).as(SelectAgent.class).select();
		assertEquals(1, listbox5.as(Listbox.class).getSelectedIndex());
		assertEquals(1, listbox6.as(Listbox.class).getSelectedIndex());
		assertEquals("1", lb2.as(Label.class).getValue());
		//how to detect error window component~~~~~~~
		
		listbox5.queryAll("listitem").get(0).as(SelectAgent.class).select();
		assertEquals(0, listbox4.as(Listbox.class).getSelectedIndex());
		assertEquals(1, listbox6.as(Listbox.class).getSelectedIndex());
		assertEquals("1", lb2.as(Label.class).getValue());
		//how to detect error window component~~~~~~~
		
		listbox6.queryAll("listitem").get(0).as(SelectAgent.class).select();
		assertEquals(0, listbox4.as(Listbox.class).getSelectedIndex());
		assertEquals(0, listbox5.as(Listbox.class).getSelectedIndex());
		assertEquals("1", lb2.as(Label.class).getValue());
		//how to detect error window component~~~~~~~
	}
}
