/** FormWithSetTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.form;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class FormWithSetTest extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent window = desktop.query("#win");
		ComponentAgent viewGrid = window.query("#view");
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		ComponentAgent listbox = window.query("listbox");
		listbox.getFirstChild().query("textbox").type("Name");
		listbox.getChild(1).query("textbox").type("MainTag");
		listbox.getLastChild().query("textbox").type("Tags");
		ComponentAgent newTagValue = listbox.query("#newTagValue");
		newTagValue.type("NewItem");
		ComponentAgent formGrid = window.query("#form");
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
		ComponentAgent saveButton = window.getLastChild().getPreviousSibling();
		saveButton.getPreviousSibling().click(); // do serialization
		
		// need to use the new component reference
		desktop.query("#win").getLastChild().getPreviousSibling().click();
		checkContent(desktop.query("#win #view"), "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
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
