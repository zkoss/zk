package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F96_ZK_4934_ListModelMapTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery window = jq("$win");
		JQuery formGrid = window.find("$form");
		JQuery listbox = window.find("@listbox");
		JQuery newTagValue = jq("$newTagValue");
		JQuery addNewTagBtn = jq("$newTagBtn");
		JQuery editRow = formGrid.find("@row");
		//buttons
		JQuery addAll = window.find("$addAll");
		JQuery removeAll = window.find("$removeAll");
		JQuery serialize = window.find("$serialize");
		JQuery save = window.find("$save");
		JQuery cancel = window.find("$cancel");

		checkContent("screw", "tool", "{metal=metal, construction=construction, small=small}");

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
		checkContent("screw", "tool", "{metal=metal, construction=construction, small=small}");

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
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");

		//remove tags and cancel
		click(listbox.find("@a").last());
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		click(cancel);
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");

		//remove tags and save
		click(listbox.find("@a").last());
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, NewItem=NewItem}");
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags}");

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
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, big=big, middle=middle}");

		//remove tags to ensure the order
		click(listbox.find("@hlayout").eq(3).find("@a"));
		waitResponse();
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle}");

		//click addAll
		click(addAll);
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle}");
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle, addAll1=addAll1, addAll2=addAll2, addAll3=addAll3}");

		//click removeAll
		click(removeAll);
		waitResponse();
		checkContent("Name", "MainTag", "{metal=Name, construction=MainTag, small=Tags, middle=middle, addAll1=addAll1, addAll2=addAll2, addAll3=addAll3}");
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "{}");

		// do serialize and deserialize, need to use the new component reference
		click(serialize); // do serialization
		waitResponse();
		window = jq("$win");
		newTagValue = jq("$newTagValue");
		addNewTagBtn = jq("$newTagBtn");
		save = window.find("$save");
		checkContent("Name", "MainTag", "{}");

		type(editRow.find("@textbox").eq(0), "chunfu");
		waitResponse();
		type(editRow.find("@textbox").eq(1), "potix");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		click(addNewTagBtn);
		waitResponse();
		click(save);
		waitResponse();
		checkContent("chunfu", "potix", "{NewItem=NewItem}");
	}

	private void checkContent(String val0, String val1, String val2) {
		assertEquals(val0, jq("$view_name").text());
		assertEquals(val1, jq("$view_main").text());
		assertEquals(val2, jq("$view_tag").text());
	}
}
