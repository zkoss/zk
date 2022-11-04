package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00743_2Test extends WebDriverTestCase {
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
		JQuery range = jq("$range");
		JQuery clean = jq("$clean");
		JQuery select = jq("$select");
		JQuery reload = jq("$reload");
		JQuery select0 = jq("$select0");
		JQuery showselect = jq("$showselect");

		JQuery items = outerbox.find("@listitem");
		click(items.eq(0));
		waitResponse();
		click(items.eq(2));
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[A, C]", range.text());

		click(clean);
		waitResponse();
		checkSelectedItems(outerbox, new int[]{});
		click(showselect);
		waitResponse();
		assertEquals("[]", range.text());

		click(items.eq(2));
		waitResponse();
		click(items.eq(4));
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[C, E]", range.text());

		click(select);
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[B, D]", range.text());

		click(select0);
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[A, B]", range.text());

		click(reload);
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[A, B]", range.text());
	}
}
