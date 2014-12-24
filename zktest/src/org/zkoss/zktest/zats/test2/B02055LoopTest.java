package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

public class B02055LoopTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent grid = desktop.query("#grid");
		ComponentAgent listbox = desktop.query("#listbox");
		String[] links = {"http://www.zkoss.org", "http://jp.zkoss.org", "http://zh.zkoss.org", "http://www.potix.com"};
		List<ComponentAgent> rows = grid.queryAll("row");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		
		assertEquals(4, rows.size());
		assertEquals(4, items.size());
		for (int i = 0; i < rows.size(); i++) {
			assertEquals(links[i], rows.get(i).query("label").as(Label.class).getValue());
		}
		for (int i = 0; i < rows.size(); i++) {
			assertEquals(links[i], items.get(i).query("label").as(Label.class).getValue());
		}
	}
}
