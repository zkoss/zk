package org.zkoss.zktest.zats.test2;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;

public class B02078Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent window = desktop.query("window");
		ComponentAgent viewGrid = desktop.query("#view");
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		ComponentAgent listbox = desktop.query("listbox");
		listbox.getFirstChild().query("textbox").type("Name");
		listbox.getChild(1).query("textbox").type("MainTag");
		listbox.getLastChild().query("textbox").type("Tags");
		ComponentAgent newTagValue = listbox.query("#newTagValue");
		newTagValue.type("NewItem");
		ComponentAgent formGrid = desktop.query("#form");
		ComponentAgent editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("Name");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("MainTag");
		newTagValue.getNextSibling().click();
		window.getLastChild().click();
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		listbox.getFirstChild().query("textbox").type("Name");
		listbox.getChild(1).query("textbox").type("MainTag");
		listbox.getLastChild().query("textbox").type("Tags");
		newTagValue.type("NewItem");
		newTagValue.getNextSibling().click();
		editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("Name");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("MainTag");
		window.getLastChild().getPreviousSibling().click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
	}
	
	private void checkContent(ComponentAgent viewGrid, String val0, String val1, String val2) {
		ComponentAgent row = viewGrid.getFirstChild().getFirstChild();
		assertEquals(val0, row.getLastChild().as(Label.class).getValue());
		row = row.getNextSibling();
		assertEquals(val1, row.getLastChild().as(Label.class).getValue());
		row = row.getNextSibling();
		assertEquals(val2, row.getLastChild().as(Label.class).getValue());
		
	}
}
