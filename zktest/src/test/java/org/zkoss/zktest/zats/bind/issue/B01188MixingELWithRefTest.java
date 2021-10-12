package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

public class B01188MixingELWithRefTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent lb = desktop.query("#lb");
		List<ComponentAgent> outerItems = lb.queryAll("listitem.outer");
		assertEquals(2, outerItems.size());
		assertEquals("Today", outerItems.get(0).query("listcell.outer > label").as(Label.class).getValue());
		assertEquals("Tomorrow", outerItems.get(1).query("listcell.outer > label").as(Label.class).getValue());
		
		List<ComponentAgent> innerItems = outerItems.get(0).queryAll("listbox > listitem.inner");
		assertEquals(2, innerItems.size());
		assertEquals("Item 1", innerItems.get(0).query("listcell.inner > label").as(Label.class).getValue());
		assertEquals("Item 2", innerItems.get(1).query("listcell.inner > label").as(Label.class).getValue());
		
		innerItems = outerItems.get(1).queryAll("listbox > listitem.inner");
		assertEquals(2, innerItems.size());
		assertEquals("Item 3", innerItems.get(0).query("listcell.inner > label").as(Label.class).getValue());
		assertEquals("Item 4", innerItems.get(1).query("listcell.inner > label").as(Label.class).getValue());
	}
}
