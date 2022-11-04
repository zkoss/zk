package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00743_1Test extends WebDriverTestCase {
	private void checkSelectedItems(JQuery listbox, int[] selectedIndexes) {
		assertEquals(selectedIndexes.length + "", listbox.toWidget().eval("getSelectedItems().length"));
		for (int selIndex : selectedIndexes) {
			assertTrue(listbox.find("@listitem").eq(selIndex).toWidget().is("selected"), "index - " + selIndex + " is not selected");
		}
	}

	@Test
	public void test() {
		connect();

		JQuery outerbox = jq("$outerbox");
		JQuery selected = jq("$selected");
		JQuery range = jq("$range");
		JQuery clean1 = jq("$clean1");
		JQuery clean2 = jq("$clean2");
		JQuery select = jq("$select");
		JQuery reload = jq("$reload");
		JQuery select0 = jq("$select0");
		JQuery showselect = jq("$showselect");

		JQuery items = outerbox.find("@listitem");
		click(items.eq(0));
		waitResponse();
		click(items.eq(2));
		waitResponse();
		assertEquals("[A, C]", selected.text());

		click(showselect);
		waitResponse();
		assertEquals("[A, C]", range.text());

		click(clean1);
		waitResponse();
		assertEquals("", selected.text());
		checkSelectedItems(outerbox, new int[]{});

		click(showselect);
		waitResponse();
		assertEquals("[]", range.text());

		click(items.eq(2));
		waitResponse();
		click(items.eq(4));
		waitResponse();
		assertEquals("[C, E]", selected.text());

		click(showselect);
		waitResponse();
		assertEquals("[C, E]", range.text());

		click(clean2);
		waitResponse();
		assertEquals("[]", selected.text());

		click(showselect);
		waitResponse();
		assertEquals("[]", range.text());

		click(select);
		waitResponse();
		assertEquals("[B, D]", selected.text());

		click(showselect);
		waitResponse();
		assertEquals("[B, D]", range.text());

		click(select0);
		waitResponse();
		assertEquals("[B, D]", selected.text());

		click(showselect);
		waitResponse();
		assertEquals("[A, B]", range.text());

		click(reload);
		waitResponse();
		assertEquals("[B, D]", selected.text());

		click(showselect);
		waitResponse();
		assertEquals("[B, D]", range.text());
	}
}
