package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.ClickAgent;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;


public class B01873SelectedItemOnClickTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#win #listbox");
		ComponentAgent lb1 = desktop.query("#win #lb1");
		ComponentAgent lb2 = desktop.query("#win #lb2");
		List<ComponentAgent> items = listbox.queryAll("listitem");

		assertEquals(3, items.size());
		
		items.get(0).as(SelectAgent.class).select();
		assertEquals("Item 1", lb1.as(Label.class).getValue());
		List<ComponentAgent> divs = items.get(0).queryAll("div");
		divs.get(0).as(ClickAgent.class).click();
		assertEquals("Item 1-sub1", lb2.as(Label.class).getValue());
		divs.get(1).as(ClickAgent.class).click();
		assertEquals("Item 1-sub2", lb2.as(Label.class).getValue());
		
		items.get(1).as(SelectAgent.class).select();
		assertEquals("Item 2", lb1.as(Label.class).getValue());
		divs = items.get(1).queryAll("div");
		divs.get(0).as(ClickAgent.class).click();
		assertEquals("Item 2-sub1", lb2.as(Label.class).getValue());
		divs.get(1).as(ClickAgent.class).click();
		assertEquals("Item 2-sub2", lb2.as(Label.class).getValue());
		
		items.get(2).as(SelectAgent.class).select();
		assertEquals("Item 3", lb1.as(Label.class).getValue());
		divs = items.get(2).queryAll("div");
		divs.get(0).as(ClickAgent.class).click();
		assertEquals("Item 3-sub1", lb2.as(Label.class).getValue());
		divs.get(1).as(ClickAgent.class).click();
		assertEquals("Item 3-sub2", lb2.as(Label.class).getValue());		
	}
}
