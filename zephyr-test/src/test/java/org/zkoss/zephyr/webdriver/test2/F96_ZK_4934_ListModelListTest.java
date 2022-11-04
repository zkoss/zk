package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F96_ZK_4934_ListModelListTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery window = jq("$win");
		JQuery formGrid = window.find("$form");
		JQuery listbox = window.find("@listbox");
		JQuery newTagValue = listbox.find("$newTagValue");
		JQuery addNewTagBtn = listbox.find("$newTagBtn");
		JQuery editRow = formGrid.find("@row");
		//buttons
		JQuery addAll = window.find("$addAll");
		JQuery retainAll = window.find("$retainAll");
		JQuery serialize = window.find("$serialize");
		JQuery save = window.find("$save");
		JQuery cancel = window.find("$cancel");
		
		//init state
		checkContent("screw", "tool", "[metal, construction, small]");
		
		//click cancel to discard all changes
		type(editRow.find("@textbox").eq(0), "Name");
		waitResponse();
		type(editRow.find("@textbox").eq(1), "MainTag");
		waitResponse();
		type(listbox.find("@listitem").eq(0).find("@textbox"), "Name");
		waitResponse();
		type(listbox.find("@listitem").eq(1).find("@textbox"), "MainTag");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(cancel);
		waitResponse();
		checkContent("screw", "tool", "[metal, construction, small]");
		
		//add tag and save
		type(editRow.find("@textbox").eq(0), "Name");
		waitResponse();
		type(editRow.find("@textbox").eq(1), "MainTag");
		waitResponse();
		type(listbox.find("@listitem").eq(0).find("@textbox"), "Name");
		waitResponse();
		type(listbox.find("@listitem").eq(1).find("@textbox"), "MainTag");
		waitResponse();
		type(listbox.find("@listitem").eq(2).find("@textbox"), "Tags");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		
		//remove tags and cancel
		click(listbox.find("@a").last());
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		click(cancel);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		
		//remove tags and save
		click(listbox.find("@a").last());
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags]");
		
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
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, big, middle, big]");
		
		//remove tags to ensure the order
		click(listbox.find("@hlayout").eq(3).find("@a"));
		waitResponse();
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, middle, big]");
		
		//click addAll
		click(addAll);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, middle, big]");
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags, middle, big, addAll1, addAll2, addAll3]");
		
		//click retainAll
		click(retainAll);
		waitResponse();
		assertEquals(3, listbox.find("@listitem").length());
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "[addAll1, addAll2, addAll3]");
		
		// do serialize and deserialize, need to use the new component reference
		click(serialize); // do serialization
		waitResponse();
		window = jq("$win");
		listbox = window.find("@listbox");
		newTagValue = listbox.find("$newTagValue");
		addNewTagBtn = listbox.find("$newTagBtn");
		save = window.find("$save");
		checkContent("Name", "MainTag", "[addAll1, addAll2, addAll3]");

		type(editRow.find("@textbox").eq(0), "chunfu");
		waitResponse();
		type(editRow.find("@textbox").eq(1), "potix");
		waitResponse();
		type(listbox.find("@listitem").eq(0).find("@textbox"), "first");
		waitResponse();
		type(listbox.find("@listitem").eq(1).find("@textbox"), "second");
		waitResponse();
		type(listbox.find("@listitem").eq(2).find("@textbox"), "third");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent("chunfu", "potix", "[first, second, third, NewItem]");

		if (window.find("$msg").text().startsWith("error :")) {
			fail("Should not cause any error message");
		}
		assertNoJSError();
	}

	private void checkContent(String val0, String val1, String val2) {
		assertEquals(val0, jq("$view_name").text());
		assertEquals(val1, jq("$view_main").text());
		assertEquals(val2, jq("$view_tag").text());
	}
}
