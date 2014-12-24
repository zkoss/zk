package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;

public class B00714Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent outerbox = desktop.query("#outerbox");
		ComponentAgent lb = desktop.query("#lb");
		ComponentAgent reload = desktop.query("#reload");
		ComponentAgent invalidate = desktop.query("#invalidate");
		List<ComponentAgent> items = outerbox.queryAll("listitem");

		assertEquals(2, items.size());
		assertEquals("A0", items.get(0).query("listcell").as(Listcell.class).getLabel());
		assertEquals("A0 0", items.get(1).query("listcell").as(Listcell.class).getLabel());
		assertEquals("", lb.as(Label.class).getValue());

		items.get(0).select();
		assertEquals("A0", lb.as(Label.class).getValue());
		
		reload.click();
		items = outerbox.queryAll("listitem");
		assertEquals("A0", items.get(0).query("listcell").as(Listcell.class).getLabel());
		assertEquals("A0 0", items.get(1).query("listcell").as(Listcell.class).getLabel());
		assertEquals("A0", lb.as(Label.class).getValue());
		
		invalidate.click();
		items = outerbox.queryAll("listitem");
		assertEquals("A0", items.get(0).query("listcell").as(Listcell.class).getLabel());
		assertEquals("A0 0", items.get(1).query("listcell").as(Listcell.class).getLabel());
		assertEquals("A0", lb.as(Label.class).getValue());
	}
}
