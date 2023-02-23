package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B02078Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery window = jq("@window");
		JQuery viewGrid = jq("$view");
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		JQuery listbox = jq("@listbox");
		type(listbox.find("@listitem").eq(0).find("@textbox"), "Name");
		waitResponse();
		type(listbox.find("@listitem").eq(1).find("@textbox"), "MainTag");
		waitResponse();
		type(listbox.find("@listitem").last().find("@textbox"), "Tags");
		waitResponse();
		JQuery newTagValue = jq("$newTagValue");
		type(newTagValue, "NewItem");
		waitResponse();
		JQuery formGrid = jq("$form");
		Widget editRow = formGrid.toWidget().firstChild().firstChild();
		type(editRow.lastChild(), "Name");
		waitResponse();
		editRow = editRow.nextSibling();
		type(editRow.lastChild(), "MainTag");
		waitResponse();
		sendKeys(newTagValue, Keys.ENTER);
		waitResponse();
		click(window.toWidget().lastChild());
		waitResponse();
		checkContent(viewGrid, "screw", "tool", "[metal, construction, small]");
		type(jq(listbox.toWidget().firstChild()).find("@textbox"), "Name");
		waitResponse();
		type(jq(listbox.toWidget().firstChild().nextSibling()).find("@textbox"), "MainTag");
		waitResponse();
		type(jq(listbox.toWidget().lastChild()).find("@textbox"), "Tags");
		waitResponse();
		type(newTagValue, "NewItem");
		waitResponse();
		sendKeys(newTagValue, Keys.ENTER);
		waitResponse();
		editRow = formGrid.toWidget().firstChild().firstChild();
		type(editRow.lastChild(), "Name");
		waitResponse();
		editRow = editRow.nextSibling();
		type(editRow.lastChild(), "MainTag");
		waitResponse();
		click(window.toWidget().lastChild().previousSibling());
		waitResponse();
		checkContent(viewGrid, "Name", "MainTag", "[Name, MainTag, Tags, NewItem]");
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
