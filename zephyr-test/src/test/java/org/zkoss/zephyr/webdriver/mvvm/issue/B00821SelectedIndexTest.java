package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00821SelectedIndexTest extends WebDriverTestCase {
	private void checkSelectedItems(JQuery listbox, int[] selectedIndexes) {
		assertEquals(selectedIndexes.length + "", listbox.toWidget().eval("getSelectedItems().length"));
		for (int selIndex : selectedIndexes) {
			assertTrue(listbox.find("@listitem").eq(selIndex).toWidget().is("selected"), "index - " + selIndex + " is not selected");
		}
	}

	@Test
	public void test() {
		connect();

		JQuery selectbox = jq("$selectbox");
		JQuery listbox = jq("$listbox");
		JQuery combobox = jq("$combobox");
		JQuery i1 = jq("$i1");

		type(i1, "1");
		waitResponse();
		checkSelectedItems(listbox, new int[]{1});
		assertEquals("1", selectbox.toWidget().get("selectedIndex"));
		assertEquals("B", combobox.toWidget().get("value"));

		type(i1, "2");
		waitResponse();
		checkSelectedItems(listbox, new int[]{2});
		assertEquals("2", selectbox.toWidget().get("selectedIndex"));
		assertEquals("C", combobox.toWidget().get("value"));
	}
}
