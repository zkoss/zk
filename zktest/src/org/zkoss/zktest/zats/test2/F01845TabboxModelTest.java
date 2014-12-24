package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class F01845TabboxModelTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent msg = desktop.query("#msg");
		ComponentAgent tbox = desktop.query("#tbox");
		ComponentAgent add = desktop.query("#add");
		ComponentAgent remove = desktop.query("#remove");
		List<ComponentAgent> tabs = tbox.queryAll("tab");

		assertEquals("Detail 1", msg.as(Label.class).getValue());
		assertEquals(2, tabs.size());
		
		tabs.get(1).select();
		assertEquals("Detail 2", msg.as(Label.class).getValue());
		
		add.click();
		add.click();
		tabs = tbox.queryAll("tab");
		assertEquals("Detail 2", msg.as(Label.class).getValue());
		assertEquals(4, tabs.size());
		
		tabs.get(2).select();
		assertEquals("Detail 3", msg.as(Label.class).getValue());
		
		remove.click();
		tabs = tbox.queryAll("tab");
		assertEquals("Detail 3", msg.as(Label.class).getValue());
		assertEquals(3, tabs.size());
	}
}
