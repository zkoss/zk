package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F96_ZK_4934_ListModelArrayTest extends ClientBindTestCase {

	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery window = jq("$win");
		JQuery formGrid = window.find("$form");
		JQuery listbox = window.find("@listbox");
		JQuery editRow = formGrid.find("@row");
		//buttons
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
		click(cancel);
		waitResponse();
		checkContent("screw", "tool", "[metal, construction, small]");

		//edit tag and save
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
		click(save);
		waitResponse();
		checkContent("Name", "MainTag", "[Name, MainTag, Tags]");

		// do serialize and deserialize, need to use the new component reference
		click(serialize); // do serialization
		waitResponse();
		window = jq("$win");
		listbox = window.find("@listbox");
		save = window.find("$save");
		checkContent("Name", "MainTag", "[Name, MainTag, Tags]");

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
		click(save);
		waitResponse();
		checkContent("chunfu", "potix", "[first, second, third]");

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
