package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F96_ZK_4934_ListModelArrayTest extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent window = desktop.query("#win");
		ComponentAgent viewGrid = window.query("#view");
		ComponentAgent formGrid = window.query("#form");
		ComponentAgent listbox = window.query("listbox");
		ComponentAgent editRow = formGrid.getFirstChild().getFirstChild();
		//buttons
		ComponentAgent serialize = window.query("#serialize");
		ComponentAgent save = window.query("#save");
		ComponentAgent cancel = window.query("#cancel");

		//init state
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");

		//click cancel to discard all changes
		editRow.getLastChild().type("Name");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("MainTag");
		listbox.getFirstChild().query("textbox").type("Name");
		listbox.getChild(1).query("textbox").type("MainTag");
		cancel.click();
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");

		//edit tag and save
		editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("Name");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("MainTag");
		listbox.getFirstChild().query("textbox").type("Name");
		listbox.getChild(1).query("textbox").type("MainTag");
		listbox.getLastChild().query("textbox").type("Tags");
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags]");

		// do serialize and deserialize, need to use the new component reference
		serialize.click(); // do serialization
		window = desktop.query("#win");
		viewGrid = window.query("#view");
		formGrid = window.query("#form");
		listbox = window.query("listbox");
		save = window.query("#save");
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags]");

		editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("chunfu");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("potix");
		listbox.getFirstChild().query("textbox").type("first");
		listbox.getChild(1).query("textbox").type("second");
		listbox.getLastChild().query("textbox").type("third");
		save.click();
		checkContent(viewGrid, "chunfu", "potix", "[first, second, third]");

		if (window.query("#msg").as(Label.class).getValue().startsWith("error :")) {
			fail("Should not cause any error message");
		}
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
