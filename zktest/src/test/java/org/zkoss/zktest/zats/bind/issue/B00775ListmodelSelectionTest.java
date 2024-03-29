package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zats.mimic.operation.SortAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listbox;

public class B00775ListmodelSelectionTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		ComponentAgent header = desktop.query("#header");
		ComponentAgent shrink = desktop.query("#shrink");
		
		header.as(SortAgent.class).sort(false);
		listbox.queryAll("listitem").get(8).as(SelectAgent.class).select();
		assertEquals(8, listbox.as(Listbox.class).getSelectedIndex());
		
		shrink.click();
		assertEquals(0, listbox.as(Listbox.class).getSelectedIndex());	
	}
}
