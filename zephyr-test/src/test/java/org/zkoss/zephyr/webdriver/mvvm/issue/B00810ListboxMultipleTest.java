package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00810ListboxMultipleTest extends WebDriverTestCase {
	private void checkSelectedItems(JQuery listbox, int[] selectedIndexes) {
		assertEquals(selectedIndexes.length + "", listbox.toWidget().eval("getSelectedItems().length"));
		for (int selIndex : selectedIndexes) {
			assertTrue(listbox.find("@listitem").eq(selIndex).toWidget().is("selected"), "index - " + selIndex + " is not selected");
		}
	}

	@Test
	public void test() {
		connect();

		JQuery listbox1 = jq("$listbox1");
		JQuery listbox2 = jq("$listbox2");
		JQuery listbox3 = jq("$listbox3");
		JQuery l1 = jq("$l1");
		JQuery toggle = jq("$toggle");
		JQuery update = jq("$update");
		click(listbox1.find("@listitem").eq(1));
		waitResponse();
		checkSelectedItems(listbox1, new int[]{1});
		checkSelectedItems(listbox2, new int[]{1});
		checkSelectedItems(listbox3, new int[]{1});
		assertEquals("[1]", l1.text());

		click(listbox2.find("@listitem").eq(3));
		waitResponse();
		checkSelectedItems(listbox1, new int[]{1, 3});
		checkSelectedItems(listbox2, new int[]{1, 3});
		checkSelectedItems(listbox3, new int[]{1, 3});
		assertEquals("[1, 3]", l1.text());

		click(listbox3.find("@listitem").eq(6));
		waitResponse();
		checkSelectedItems(listbox1, new int[]{1, 3, 6});
		checkSelectedItems(listbox2, new int[]{1, 3, 6});
		checkSelectedItems(listbox3, new int[]{1, 3, 6});
		assertEquals("[1, 3, 6]", l1.text());

		click(toggle);
		waitResponse();
		click(listbox3.find("@listitem").eq(7)); //can't use multiple select
		waitResponse();
		checkSelectedItems(listbox1, new int[]{7});
		checkSelectedItems(listbox2, new int[]{7});
		checkSelectedItems(listbox3, new int[]{7});
		assertEquals("[7]", l1.text());

		click(listbox3.find("@listitem").eq(1)); //can't use multiple select
		waitResponse();
		checkSelectedItems(listbox1, new int[]{1});
		checkSelectedItems(listbox2, new int[]{1});
		checkSelectedItems(listbox3, new int[]{1});
		assertEquals("[1]", l1.text());
	}
}
