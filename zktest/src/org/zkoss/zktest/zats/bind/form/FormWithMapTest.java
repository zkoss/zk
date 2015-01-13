/** FormWithListTest.java.

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
public class FormWithMapTest extends ZATSTestCase {

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
		ComponentAgent removeAll = window.query("#removeAll");
		ComponentAgent serialize = window.query("#serialize");
		ComponentAgent save = window.query("#save");
		ComponentAgent cancel = window.query("#cancel");
		
		checkContent(viewGrid, "screw", "tool", "{metal=metal, construction=construction, small=small}");
		
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
		checkContent(viewGrid, "screw", "tool", "{metal=metal, construction=construction, small=small}");
		
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
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		
		//remove tags and cancel
		listbox.getLastChild().query("textbox").getNextSibling().click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		cancel.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		
		//remove tags and save
		listbox.getLastChild().query("textbox").getNextSibling().click();;
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags}");
		
		//add multiple tags and save
		newTagValue.type("big");
		addNewTagBtn.click();
		newTagValue.type("middle");
		addNewTagBtn.click();
		newTagValue.type("big");
		addNewTagBtn.click();
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, big=big, middle=middle}");
		
		//remove tags to ensure the order
		listbox.getChild(3).query("textbox").getNextSibling().click();
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle}");
		
		//click addAll
		addAll.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle}");
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle, addAll1=addAll1, addAll2=addAll2, addAll3=addAll3}");
		
		//click removeAll
		removeAll.click();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle, addAll1=addAll1, addAll2=addAll2, addAll3=addAll3}");
		save.click();
		checkContent(viewGrid, "Name", "MainTag", "{}");
		
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
		checkContent(viewGrid, "Name", "MainTag", "{}");
		
		editRow = formGrid.getFirstChild().getFirstChild();
		editRow.getLastChild().type("chunfu");
		editRow = editRow.getNextSibling();
		editRow.getLastChild().type("potix");
		newTagValue.type("NewItem");
		addNewTagBtn.click();
		save.click();
		checkContent(viewGrid, "chunfu", "potix", "{NewItem=NewItem}");
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
