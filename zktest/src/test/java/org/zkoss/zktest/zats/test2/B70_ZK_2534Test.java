/* B70_ZK_2534Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 12:41:40 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2534Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		testListbox(jq("@listbox:eq(1)"));
		testListbox(jq("@listbox:eq(3)"));
	}

	@Test
	public void testPaging() {
		connect();
		testPagingListbox(jq("@listbox:eq(0)"));
		testPagingListbox(jq("@listbox:eq(2)"));
	}

	@Test
	public void testSelectRange() {
		connect();

		testListboxSelectRange(jq("@listbox:eq(1)"));
		testListboxSelectRange(jq("@listbox:eq(3)"));
	}

	private void testListbox(JQuery lb) {
		click(lb.find(".z-listheader-checkable"));
		waitResponse();

		// Force to load all first.
		JQuery body = lb.find(".z-listbox-body");
		int contentHeight = body.scrollHeight();
		for (int i = 1; i <= 20; i++) {
			body.scrollTop(contentHeight / 20 * i);
			waitResponse();
		}

		JQuery selectableItems = lb.find("@listitem:has(.z-listitem-checkable:not(.z-listitem-disabled))");
		selectableItems
				.iterator()
				.forEachRemaining(item -> Assert.assertTrue(item.hasClass("z-listitem-selected")));

		click(selectableItems.last().find(".z-listitem-checkable"));
		body.scrollTop(0);
		waitResponse();
		Assert.assertFalse(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));
	}

	private void testPagingListbox(JQuery lb) {
		click(lb.find(".z-listheader-checkable"));
		waitResponse();

		JQuery selectableItems = lb.find("@listitem:has(.z-listitem-checkable:not(.z-listitem-disabled))");
		JQuery next = lb.find("@paging .z-paging-next:not([disabled])");
		do {
			selectableItems
					.iterator()
					.forEachRemaining(item -> Assert.assertTrue(item.hasClass("z-listitem-selected")));
		} while (isPageable(next));

		click(selectableItems.last().find(".z-listitem-checkable"));
		waitResponse();

		JQuery previous = lb.find("@paging .z-paging-previous:not([disabled])");
		do {
			Assert.assertFalse(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));
		} while (isPageable(previous));
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

	private void testListboxSelectRange(JQuery lb) {
		JQuery selectableItems = lb.find("@listitem:has(.z-listitem-checkable:not(.z-listitem-disabled))");
		JQuery firstSelectableRow = selectableItems.first();
		JQuery firstCheckbox = firstSelectableRow.find(".z-listitem-checkable");
		click(firstCheckbox);
		waitResponse();

		JQuery body = lb.find(".z-listbox-body");
		body.scrollTop(body.scrollHeight());
		waitResponse();

		new Actions(driver)
				.keyDown(Keys.SHIFT)
				.click(toElement(selectableItems.last().find(".z-listitem-checkable")))
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		Assert.assertTrue(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));

		body.scrollTop(0);
		waitResponse();
		Assert.assertTrue(lb.find(".z-listheader-checkable").hasClass("z-listheader-checked"));
		Assert.assertTrue(firstSelectableRow.hasClass("z-listitem-selected"));
	}
}
