package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B01895PagingOnListbox3Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent listbox = desktop.query("#listbox");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		assertTrue(items.size() > 0);
	}
}
