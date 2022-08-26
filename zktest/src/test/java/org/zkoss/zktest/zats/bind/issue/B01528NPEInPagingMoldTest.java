package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

public class B01528NPEInPagingMoldTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		items.get(9).select();
		
		assertEquals("Item 9", desktop.query("#tb").as(Textbox.class).getValue());
		
		desktop.query("#delete").click();
		String v = desktop.query("#tb").as(Textbox.class).getValue();
		assertTrue(v == null || v.equals(""));
		
		items = listbox.queryAll("listitem");
		assertEquals(9, items.size());
	}
}
