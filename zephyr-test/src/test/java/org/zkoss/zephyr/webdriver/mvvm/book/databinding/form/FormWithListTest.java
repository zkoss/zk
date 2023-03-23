/** FormWithListTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.form;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author jumperchen
 */
public class FormWithListTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery window = jq("$win");
		JQuery viewGrid = window.find("$view");
		JQuery formGrid = window.find("$form");
		JQuery listbox = window.find("@listbox");
		JQuery newTagValue = jq("$newTagValue");
		Widget addNewTagBtn = newTagValue.toWidget().nextSibling();
		Widget editRow = formGrid.find("@row").toWidget();
		//buttons
		JQuery addAll = window.find("$addAll");
		JQuery retainAll = window.find("$retainAll");
		JQuery serialize = window.find("$serialize");
		JQuery save = window.find("$save");
		JQuery cancel = window.find("$cancel");

		//init state
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");

		//click cancel to discard all changes
		type(editRow.lastChild(), "Name");
		waitResponse();
		editRow = editRow.nextSibling();
		type(editRow.lastChild(), "MainTag");
		waitResponse();
		type(listbox.find("@textbox").eq(0), "Name");
		waitResponse();
		type(listbox.find("@textbox").eq(1), "MainTag");
		waitResponse();
		click(listbox.find("@listitem").eq(2).find("@a"));
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(cancel);
		waitResponse();
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		//add tag and save
		editRow = formGrid.toWidget().firstChild().firstChild();
		type(editRow.lastChild(), "Name");
		waitResponse();
		editRow = editRow.nextSibling();
		type(editRow.lastChild(), "MainTag");
		waitResponse();
		type(listbox.find("@textbox").eq(0), "Name");
		waitResponse();
		type(listbox.find("@textbox").eq(1), "MainTag");
		waitResponse();
		type(listbox.find("@textbox").eq(2), "Tags");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		//remove tags and cancel
		click(listbox.find("@textbox").last().toWidget().nextSibling());
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		click(cancel);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		//remove tags and save
		click(listbox.find("@textbox").last().toWidget().nextSibling());
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags]");
		//add multiple tags and save
		type(newTagValue, "big");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		type(newTagValue, "middle");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		type(newTagValue, "big");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, big, middle, big]");
		//remove tags to ensure the order
		click(listbox.find("@textbox").last().toWidget().nextSibling());
		waitResponse();
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big]");
		//click addAll
		click(addAll);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big]");
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, middle, big, addAll1, addAll2, addAll3]");
		//click retainAll
		click(retainAll);
		waitResponse();
		assertEquals(3, listbox.toWidget().nChildren());
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[addAll1, addAll2, addAll3]");
		// do serialize and deserialize, need to use the new component reference
		click(serialize);
		waitResponse(); // do serialization
		window = jq("$win");
		viewGrid = window.find("$view");
		formGrid = window.find("$form");
		listbox = window.find("@listbox");
		addNewTagBtn = newTagValue.toWidget().nextSibling();
		save = window.find("$save");
		checkContent(viewGrid, "Name", "MainTag", "[addAll1, addAll2, addAll3]");
		editRow = formGrid.toWidget().firstChild().firstChild();
		type(editRow.lastChild(), "chunfu");
		waitResponse();
		editRow = editRow.nextSibling();
		type(editRow.lastChild(), "potix");
		waitResponse();
		type(listbox.find("@textbox").eq(0), "first");
		waitResponse();
		type(listbox.find("@textbox").eq(1), "second");
		waitResponse();
		type(listbox.find("@textbox").eq(2), "third");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent(viewGrid, "chunfu", "potix", "[first, second, third, NewItem]");
		if (window.find("$msg").text().startsWith("error :")) {
			fail("Should not cause any error message");
		}
	}

	private void checkContent(JQuery viewGrid, String val0, String val1, String val2) {
		Widget row = viewGrid.toWidget().firstChild().firstChild();
		assertEquals(val0, row.lastChild().get("value"));
		row = row.nextSibling();
		assertEquals(val1, row.lastChild().get("value"));
		row = row.nextSibling();
		assertEquals(val2, row.lastChild().get("value"));
	}
}
