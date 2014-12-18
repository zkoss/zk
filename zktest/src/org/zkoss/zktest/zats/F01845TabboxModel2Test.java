package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class F01845TabboxModel2Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent msg = desktop.query("#msg");
		ComponentAgent tbox = desktop.query("#tbox");
		ComponentAgent add = desktop.query("#add");
		ComponentAgent remove = desktop.query("#remove");
		List<ComponentAgent> tabs = tbox.queryAll("tab");

		assertEquals("0", msg.as(Label.class).getValue());
		assertEquals(2, tabs.size());
		
		tabs.get(1).select();
		assertEquals("1", msg.as(Label.class).getValue());
		
		add.click();
		add.click();
		tabs = tbox.queryAll("tab");
		assertEquals("1", msg.as(Label.class).getValue());
		assertEquals(4, tabs.size());
		
		tabs.get(2).select();
		assertEquals("2", msg.as(Label.class).getValue());
		
		remove.click();
		tabs = tbox.queryAll("tab");
		assertEquals("2", msg.as(Label.class).getValue());
		assertEquals(3, tabs.size());
	}
}
