/** FormWithListTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author jumperchen
 */
public class FormWithMapTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery window = jq("$win");
		JQuery viewGrid = window.find("$view");
		JQuery formGrid = window.find("$form");
		JQuery listbox = window.find("@listbox");
		JQuery newTagValue = jq("$newTagValue");
		Widget addNewTagBtn = newTagValue.toWidget().nextSibling();
		Widget editRow = formGrid.find("@row").toWidget();
		//buttons
		JQuery addAll = window.find("$addAll");
		JQuery removeAll = window.find("$removeAll");
		JQuery serialize = window.find("$serialize");
		JQuery save = window.find("$save");
		JQuery cancel = window.find("$cancel");

		checkContent(viewGrid, "screw", "tool", "{metal=metal, construction=construction, small=small}");

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
		checkContent(viewGrid, "screw", "tool", "{metal=metal, construction=construction, small=small}");

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
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");

		//remove tags and cancel
		click(listbox.find("@textbox").last().toWidget().nextSibling());
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		click(cancel);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		//remove tags and save
		click(listbox.find("@textbox").last().toWidget().nextSibling());
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags}");
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
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, big=big, middle=middle}");
		//remove tags to ensure the order
		click(listbox.find("@textbox").eq(3).toWidget().nextSibling());
		waitResponse();
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle}");
		//click addAll
		click(addAll);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle}");
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle, addAll1=addAll1, addAll2=addAll2, addAll3=addAll3}");
		//click removeAll
		click(removeAll);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle, addAll1=addAll1, addAll2=addAll2, addAll3=addAll3}");
		click(save);
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "{}");
		// do serialize and deserialize, need to use the new component reference
		click(serialize);
		waitResponse(); // do serialization
		window = jq("$win");
		viewGrid = window.find("$view");
		formGrid = window.find("$form");
		newTagValue = jq("$newTagValue");
		addNewTagBtn = newTagValue.toWidget().nextSibling();
		save = window.find("$save");
		checkContent(viewGrid, "Name", "MainTag", "{}");

		editRow = formGrid.toWidget().firstChild().firstChild();
		type(editRow.lastChild(), "chunfu");
		waitResponse();
		editRow = editRow.nextSibling();
		type(editRow.lastChild(), "potix");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent(viewGrid, "chunfu", "potix", "{NewItem=NewItem}");
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
