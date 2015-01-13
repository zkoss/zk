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
		ComponentAgent formGrid = window.query("#form");
		ComponentAgent listbox = window.query("listbox");
		ComponentAgent newTagValue = listbox.query("#newTagValue");
		ComponentAgent addNewTagBtn = newTagValue.getNextSibling();
		ComponentAgent editRow = formGrid.getFirstChild().getFirstChild();
		//buttons
		ComponentAgent addAll = window.query("#addAll");
		ComponentAgent retainAll = window.query("#retainAll");
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
		listbox.getLastChild().query("textbox").getNextSibling().click();
		newTagValue.type("NewItem");
		addNewTagBtn.click();
		cancel.click();
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		
		//add tag and save
		editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("Name");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("MainTag");
		listbox.getFirstChild().query("textbox").type("Name");
		listbox.getChild(1).query("textbox").type("MainTag");
		listbox.getLastChild().query("textbox").type("Tags");
		newTagValue.type("NewItem");
		addNewTagBtn.click();
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		
		//remove tags and cancel
		listbox.getLastChild().query("textbox").getNextSibling().click();;
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		cancel.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		
		//remove tags and save
		listbox.getLastChild().query("textbox").getNextSibling().click();;
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags]");
		
		//add multiple tags and save
		newTagValue.type("big");
		addNewTagBtn.click();
		newTagValue.type("middle");
		addNewTagBtn.click();
		newTagValue.type("big");
		addNewTagBtn.click();
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, big, middle]");
		
		//remove tags to ensure the order
		listbox.getChild(3).query("textbox").getNextSibling().click();
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle]");
		
		//click addAll
		addAll.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle]");
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, addAll1, addAll2, addAll3]");
		
		//click retainAll
		retainAll.click();
		assertEquals(3, listbox.getChildren().size());
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "[addAll1, addAll2, addAll3]");
		
		// do serialize and deserialize, need to use the new component reference
		serialize.click(); // do serialization
		window = desktop.query("#win");
		viewGrid = window.query("#view");
		formGrid = window.query("#form");
		listbox = window.query("listbox");
		newTagValue = listbox.query("#newTagValue");
		addNewTagBtn = newTagValue.getNextSibling();
		editRow = formGrid.getFirstChild().getFirstChild();
		save = window.query("#save");
		checkContent(viewGrid, "Name", "MainTag", "[addAll1, addAll2, addAll3]");
		//checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, addAll1, addAll2, addAll3]");
		
		editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("chunfu");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("potix");
		listbox.getFirstChild().query("textbox").type("first");
		listbox.getChild(1).query("textbox").type("second");
		listbox.getLastChild().query("textbox").type("third");
		newTagValue.type("NewItem");
		addNewTagBtn.click();
		save.click();
		checkContent(viewGrid, "chunfu", "potix", "[first, second, third, NewItem]");
		//checkContent(viewGrid, "chunfu", "potix", "[first, second, Tags, middle, addAll1, addAll2, third, NewItem]");
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
