package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class F00743_2Test extends ZATSTestCase {
	
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
		
		ComponentAgent outerbox = desktop.query("#outerbox");
		ComponentAgent range = desktop.query("#range");
		ComponentAgent clean = desktop.query("#clean");
		ComponentAgent select = desktop.query("#select");
		ComponentAgent reload = desktop.query("#reload");
		ComponentAgent select0 = desktop.query("#select0");
		ComponentAgent showselect = desktop.query("#showselect");
		
		List<ComponentAgent> items = outerbox.queryAll("listitem");
		items.get(0).as(MultipleSelectAgent.class).select();
		items.get(2).as(MultipleSelectAgent.class).select();
		showselect.click();
		assertEquals("[A, C]", range.as(Label.class).getValue());
		
		clean.click();
		assertEquals("[]", getSelectedIndices(outerbox));
		showselect.click();
		assertEquals("[]", range.as(Label.class).getValue());
		
		items.get(2).as(MultipleSelectAgent.class).select();
		items.get(4).as(MultipleSelectAgent.class).select();
		showselect.click();
		assertEquals("[C, E]", range.as(Label.class).getValue());
		
		select.click();
		showselect.click();
		assertEquals("[B, D]", range.as(Label.class).getValue());
		
		select0.click();
		showselect.click();
		assertEquals("[A, B]", range.as(Label.class).getValue());
		
		reload.click();
		showselect.click();
		assertEquals("[A, B]", range.as(Label.class).getValue());
	}
}
