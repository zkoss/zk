package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;

public class F00843SelectboxSelectedItemTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent selectbox = desktop.query("#selectbox");
		ComponentAgent listbox = desktop.query("#listbox");
		ComponentAgent combobox = desktop.query("#combobox");
		ComponentAgent l1 = desktop.query("#l1");
		List<ComponentAgent> items = listbox.queryAll("listitem");
		
		items.get(1).select();
		assertEquals(1, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(1, selectbox.as(Selectbox.class).getSelectedIndex());
		assertEquals("B", combobox.as(Combobox.class).getText());
		assertEquals("B", l1.as(Label.class).getValue());
		
		items.get(2).select();
		assertEquals(2, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(2, selectbox.as(Selectbox.class).getSelectedIndex());
		assertEquals("C", combobox.as(Combobox.class).getText());
		assertEquals("C", l1.as(Label.class).getValue());
	}
}
