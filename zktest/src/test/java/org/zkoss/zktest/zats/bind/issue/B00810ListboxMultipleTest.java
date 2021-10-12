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
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;

public class B00810ListboxMultipleTest extends ZATSTestCase {
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
		
		ComponentAgent listbox1 = desktop.query("#listbox1");
		ComponentAgent listbox2 = desktop.query("#listbox2");
		ComponentAgent listbox3 = desktop.query("#listbox3");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent toggle = desktop.query("#toggle");
		ComponentAgent update = desktop.query("#update");
		
		listbox1.queryAll("listitem").get(1).as(MultipleSelectAgent.class).select();
		assertEquals("[1]", getSelectedIndices(listbox1));
		assertEquals("[1]", getSelectedIndices(listbox2));
		assertEquals("[1]", getSelectedIndices(listbox3));
		assertEquals("[1]", l1.as(Label.class).getValue());
		
		listbox2.queryAll("listitem").get(3).as(MultipleSelectAgent.class).select();
		assertEquals("[1, 3]", getSelectedIndices(listbox1));
		assertEquals("[1, 3]", getSelectedIndices(listbox2));
		assertEquals("[1, 3]", getSelectedIndices(listbox3));
		assertEquals("[1, 3]", l1.as(Label.class).getValue());
		
		listbox3.queryAll("listitem").get(6).as(MultipleSelectAgent.class).select();
		assertEquals("[1, 3, 6]", getSelectedIndices(listbox1));
		assertEquals("[1, 3, 6]", getSelectedIndices(listbox2));
		assertEquals("[1, 3, 6]", getSelectedIndices(listbox3));
		assertEquals("[1, 3, 6]", l1.as(Label.class).getValue());
		
		toggle.click();
		listbox3.queryAll("listitem").get(7).as(SelectAgent.class).select(); //can't use multiple select
		assertEquals("[7]", getSelectedIndices(listbox1));
		assertEquals("[7]", getSelectedIndices(listbox2));
		assertEquals("[7]", getSelectedIndices(listbox3));
		assertEquals("[7]", l1.as(Label.class).getValue());
		
		listbox3.queryAll("listitem").get(1).as(SelectAgent.class).select(); //can't use multiple select
		assertEquals("[1]", getSelectedIndices(listbox1));
		assertEquals("[1]", getSelectedIndices(listbox2));
		assertEquals("[1]", getSelectedIndices(listbox3));
		assertEquals("[1]", l1.as(Label.class).getValue());
	}
}
