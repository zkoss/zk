package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B01895PagingOnListbox2Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent tab4 = desktop.query("#tab4");
		ComponentAgent listbox = desktop.query("#listbox");
		
		assertEquals(null, listbox);
		tab4.select();
		listbox = desktop.query("#listbox");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		assertTrue(items.size() > 0);
	}
}
