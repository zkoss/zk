package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class B01595SelectedItemsTest extends ZATSTestCase{
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
		
		ComponentAgent listbox = desktop.query("listbox");
		ComponentAgent lb = desktop.query("#lb");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		
		assertEquals(10, items.size());
		assertEquals("[]", lb.as(Label.class).getValue());
		
		items.get(2).as(MultipleSelectAgent.class).select();
		assertEquals("[2]", lb.as(Label.class).getValue());
		
		items.get(3).as(MultipleSelectAgent.class).select();
		items.get(9).as(MultipleSelectAgent.class).select();
		assertEquals("[2, 3, 9]", lb.as(Label.class).getValue());
		
		items.get(7).as(MultipleSelectAgent.class).select();
		items.get(5).as(MultipleSelectAgent.class).select();
		items.get(1).as(MultipleSelectAgent.class).select();
		assertEquals("[2, 3, 9, 7, 5, 1]", lb.as(Label.class).getValue());
		
		items.get(5).as(MultipleSelectAgent.class).deselect();;
		items.get(3).as(MultipleSelectAgent.class).deselect();;
		items.get(0).as(MultipleSelectAgent.class).select();
		assertEquals("[2, 9, 7, 1, 0]", lb.as(Label.class).getValue());
	}
}
