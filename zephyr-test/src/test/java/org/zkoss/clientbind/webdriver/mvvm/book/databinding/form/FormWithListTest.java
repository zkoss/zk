/** FormWithListTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.form;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author jumperchen
 *
 */
public class FormWithListTest extends ClientBindTestCase {

	@Test
	public void test() {
		//		DesktopAgent desktop = connect();
		//		JQuery window = jq("$win");
		//		JQuery viewGrid = window.query("$view");
		//		JQuery formGrid = window.query("$form");
		//		JQuery listbox = window.query("listbox");
		//		JQuery newTagValue = listbox.query("$newTagValue");
		//		JQuery addNewTagBtn = newTagValue.getNextSibling();
		//		JQuery editRow = formGrid.getFirstChild().getFirstChild();
		//		//buttons
		//		JQuery addAll = window.query("$addAll");
		//		JQuery retainAll = window.query("$retainAll");
		//		JQuery serialize = window.query("$serialize");
		//		JQuery save = window.query("$save");
		//		JQuery cancel = window.query("$cancel");
		//
		//		//init state
		//		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		//
		//		//click cancel to discard all changes
		//		editRow.getLastChild().type("Name");
		//		editRow = editRow.getNextSibling();
		//		editRow.getLastChild().type("MainTag");
		//		listbox.getFirstChild().query("textbox").type("Name");
		//		listbox.getChild(1).query("textbox").type("MainTag");
		//		listbox.getLastChild().query("textbox").getNextSibling().click();
		//		newTagValue.type("NewItem");
		//		addNewTagBtn.click();
		//		cancel.click();
		//		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		//
		//		//add tag and save
		//		editRow = formGrid.getFirstChild().getFirstChild();
		//		editRow.getLastChild().type("Name");
		//		editRow = editRow.getNextSibling();
		//		editRow.getLastChild().type("MainTag");
		//		listbox.getFirstChild().query("textbox").type("Name");
		//		listbox.getChild(1).query("textbox").type("MainTag");
		//		listbox.getLastChild().query("textbox").type("Tags");
		//		newTagValue.type("NewItem");
		//		addNewTagBtn.click();
		//		save.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		//
		//		//remove tags and cancel
		//		listbox.getLastChild().query("textbox").getNextSibling().click();;
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		//		cancel.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		//
		//		//remove tags and save
		//		listbox.getLastChild().query("textbox").getNextSibling().click();;
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		//		save.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags]");
		//
		//		//add multiple tags and save
		//		newTagValue.type("big");
		//		addNewTagBtn.click();
		//		newTagValue.type("middle");
		//		addNewTagBtn.click();
		//		newTagValue.type("big");
		//		addNewTagBtn.click();
		//		save.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, big, middle, big]");
		//
		//		//remove tags to ensure the order
		//		listbox.getChild(3).query("textbox").getNextSibling().click();
		//		save.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big]");
		//
		//		//click addAll
		//		addAll.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big]");
		//		save.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big, addAll1, addAll2, addAll3]");
		//
		//		//click retainAll
		//		retainAll.click();
		//		assertEquals(3, listbox.getChildren().size());
		//		save.click();
		//		checkContent(viewGrid, "Name", "MainTag", "[addAll1, addAll2, addAll3]");
		//
		//		// do serialize and deserialize, need to use the new component reference
		//		serialize.click(); // do serialization
		//		window = jq("$win");
		//		viewGrid = window.query("$view");
		//		formGrid = window.query("$form");
		//		listbox = window.query("listbox");
		//		newTagValue = listbox.query("$newTagValue");
		//		addNewTagBtn = newTagValue.getNextSibling();
		//		editRow = formGrid.getFirstChild().getFirstChild();
		//		save = window.query("$save");
		//		checkContent(viewGrid, "Name", "MainTag", "[addAll1, addAll2, addAll3]");
		//		//checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big, addAll1, addAll2, addAll3]");
		//
		//		editRow = formGrid.getFirstChild().getFirstChild();
		//		editRow.getLastChild().type("chunfu");
		//		editRow = editRow.getNextSibling();
		//		editRow.getLastChild().type("potix");
		//		listbox.getFirstChild().query("textbox").type("first");
		//		listbox.getChild(1).query("textbox").type("second");
		//		listbox.getLastChild().query("textbox").type("third");
		//		newTagValue.type("NewItem");
		//		addNewTagBtn.click();
		//		save.click();
		//		checkContent(viewGrid, "chunfu", "potix", "[first, second, third, NewItem]");
		//		//checkContent(viewGrid, "chunfu", "potix", "[first, second, Tags, middle, big, addAll1, addAll2, third, NewItem]");
		//
		//		if (window.query("$msg").text().startsWith("error :")) {
		//			fail("Should not cause any error message");
		//		}
	}

	//	private void checkContent(JQuery viewGrid, String val0, String val1, String val2) {
	//		JQuery row = viewGrid.getFirstChild().getFirstChild();
	//		assertEquals(val0, row.getLastChild().text());
	//		row = row.getNextSibling();
	//		assertEquals(val1, row.getLastChild().text());
	//		row = row.getNextSibling();
	//		assertEquals(val2, row.getLastChild().text());
	//
	//	}
}
