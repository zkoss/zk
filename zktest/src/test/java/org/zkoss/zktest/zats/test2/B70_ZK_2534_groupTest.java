/* B70_ZK_2534_groupTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 28 16:46:23 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2534_groupTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		testListbox(jq("@listbox:eq(1)"), false);
		testListbox(jq("@listbox:eq(3)"), true);
	}

	@Test
	public void testPaging() {
		connect();
		testPagingListbox(jq("@listbox:eq(0)"), false);
		testPagingListbox(jq("@listbox:eq(2)"), true);
	}

	@Test
	public void testSelectRange() {
		connect();

		testListboxSelectRange(jq("@listbox:eq(1)"), false);
		testListboxSelectRange(jq("@listbox:eq(3)"), true);
	}

	private void testPagingListbox(JQuery lb, boolean groupSelectable) {
		click(lb.find(".z-listheader-checkable"));
		waitResponse();

		JQuery next = lb.find("@paging .z-paging-next:not([disabled])");
		do {
			lb.find("@listitem").iterator()
					.forEachRemaining(item -> Assertions.assertTrue(item.hasClass("z-listitem-selected")));
			if (groupSelectable) {
				lb.find("@listgroup").iterator()
						.forEachRemaining(item -> Assertions.assertTrue(item.hasClass("z-listgroup-selected")));
			}
		} while (isPageable(next));

		click(lb.find("@listitem:last .z-listitem-checkable"));
		waitResponse();

		JQuery previous = lb.find("@paging .z-paging-previous:not([disabled])");
		do {
			Assertions.assertFalse(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));
		} while (isPageable(previous));
	}

	private void testListbox(JQuery lb, boolean groupSelectable) {
		click(lb.find(".z-listheader-checkable"));
		waitResponse();

		// It seems listbox with group don't use ROD completely. Force to load all first.
		JQuery body = lb.find(".z-listbox-body");
		int contentHeight = body.scrollHeight();
		for (int i = 1; i <= 20; i++) {
			body.scrollTop(contentHeight / 20 * i);
			waitResponse();
		}

		lb.find("@listitem").iterator()
				.forEachRemaining(item -> Assertions.assertTrue(item.hasClass("z-listitem-selected")));
		if (groupSelectable) {
			lb.find("@listgroup").iterator()
					.forEachRemaining(item -> Assertions.assertTrue(item.hasClass("z-listgroup-selected")));
		}

		click(lb.find("@listitem:last .z-listitem-checkable"));
		body.scrollTop(0);
		waitResponse();
		Assertions.assertFalse(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));
	}

	private boolean isPageable(JQuery control) {
		if (control.exists()) {
			click(control);
			waitResponse();
			return true;
		} else {
			return false;
		}
	}

	private void testListboxSelectRange(JQuery lb, boolean groupSelectable) {
		JQuery firstSelectableRow = groupSelectable ? lb.find("@listgroup:first") : lb.find("@listitem:first");
		JQuery firstCheckbox = firstSelectableRow.find(groupSelectable ? ".z-listgroup-checkable" : ".z-listitem-checkable");
		click(firstCheckbox);
		waitResponse();

		JQuery body = lb.find(".z-listbox-body");
		body.scrollTop(body.scrollHeight());
		waitResponse();

		new Actions(driver)
				.keyDown(Keys.SHIFT)
				.click(toElement(lb.find("@listitem:last .z-listitem-checkable")))
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		Assertions.assertTrue(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));

		body.scrollTop(0);
		waitResponse();
		Assertions.assertTrue(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));
		String selectedClass = groupSelectable ? "z-listgroup-selected" : "z-listitem-selected";
		Assertions.assertTrue(firstSelectableRow.hasClass(selectedClass));
	}
}
