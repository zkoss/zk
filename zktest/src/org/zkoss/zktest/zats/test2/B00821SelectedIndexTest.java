package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Selectbox;

public class B00821SelectedIndexTest extends ZATSTestCase {
	String getSelectedIndices(ComponentAgent listbox) {
		Set<Listitem> sels = listbox.as(Listbox.class).getSelectedItems();
		ArrayList<Integer> selind = new ArrayList<Integer>();
		for (Listitem i : sels) {
			selind.add(i.getIndex());
		}
		
		return selind.toString();
	}
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent selectbox = desktop.query("#selectbox");
		ComponentAgent listbox = desktop.query("#listbox");
		ComponentAgent combobox = desktop.query("#combobox");
		ComponentAgent i1 = desktop.query("#i1");
		
		i1.type("1");
		assertEquals("[1]", getSelectedIndices(listbox));
		assertEquals(1, selectbox.as(Selectbox.class).getSelectedIndex());
		assertEquals("B", combobox.as(Combobox.class).getValue());
		
		i1.type("2");
		assertEquals("[2]", getSelectedIndices(listbox));
		assertEquals(2, selectbox.as(Selectbox.class).getSelectedIndex());
		assertEquals("C", combobox.as(Combobox.class).getValue());
	}
}
