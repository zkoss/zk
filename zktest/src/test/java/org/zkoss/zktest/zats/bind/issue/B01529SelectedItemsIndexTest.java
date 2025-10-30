package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01529SelectedItemsIndexTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		ComponentAgent lb = desktop.query("#lb");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		
		items.get(1).as(MultipleSelectAgent.class).select();
		assertEquals("[1]", lb.as(Label.class).getValue());
		
		items.get(8).as(MultipleSelectAgent.class).select();
		assertEquals("[1, 8]", lb.as(Label.class).getValue());
		
		items.get(9).as(MultipleSelectAgent.class).select();
		assertEquals("[1, 8, 9]", lb.as(Label.class).getValue());
		
		items.get(4).as(MultipleSelectAgent.class).select();
		assertEquals("[1, 8, 9, 4]", lb.as(Label.class).getValue());
		
		items.get(8).as(MultipleSelectAgent.class).deselect();;
		assertEquals("[1, 9, 4]", lb.as(Label.class).getValue());
		
		items.get(8).as(MultipleSelectAgent.class).select();
		assertEquals("[1, 9, 4, 8]", lb.as(Label.class).getValue());
		
		items.get(1).as(MultipleSelectAgent.class).deselect();
		assertEquals("[9, 4, 8]", lb.as(Label.class).getValue());
		
		items.get(2).as(MultipleSelectAgent.class).select();
		assertEquals("[9, 4, 8, 2]", lb.as(Label.class).getValue());
	}
}
