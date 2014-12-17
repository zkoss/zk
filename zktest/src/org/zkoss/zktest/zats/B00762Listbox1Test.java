package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

public class B00762Listbox1Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent outerbox = desktop.query("#outerbox");
		ComponentAgent selected = desktop.query("#selected");
		ComponentAgent min = desktop.query("#min");
		ComponentAgent clean = desktop.query("#clean");
		ComponentAgent select = desktop.query("#select");
		ComponentAgent showselect = desktop.query("#showselect");
		
		outerbox.queryAll("listitem").get(0).as(SelectAgent.class).select();
		assertEquals("A", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("0", min.as(Label.class).getValue());
		
		outerbox.queryAll("listitem").get(2).as(SelectAgent.class).select();
		assertEquals("C", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("2", min.as(Label.class).getValue());
		
		clean.click();
		assertEquals(-1, outerbox.as(Listbox.class).getSelectedIndex());
		assertEquals("", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("-1", min.as(Label.class).getValue());
		
		select.click();
		assertEquals(1, outerbox.as(Listbox.class).getSelectedIndex());
		assertEquals("B", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("1", min.as(Label.class).getValue());	
	}
}
