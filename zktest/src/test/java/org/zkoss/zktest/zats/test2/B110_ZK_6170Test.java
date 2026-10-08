/* B110_ZK_6170Test.java

	Purpose:

	Description:

	History:
		Mon Oct 05 18:00:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6170Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int listboxLeft = jq(".z-listbox").offsetLeft();
		int listboxRight = listboxLeft + jq(".z-listbox-body").outerWidth();
		int frozenColWidth = jq(".z-listcell").eq(0).outerWidth();
		int viewLeft = listboxLeft + frozenColWidth;

		JQuery input = jq(".z-textbox").eq(1); // Header 3
		int inputLeft = input.offsetLeft();
		Assertions.assertTrue(inputLeft < listboxRight && inputLeft + input.outerWidth() > listboxRight,
				"the input should be cut off at the right edge");
		clickVisiblePartAndCheck(input, viewLeft, listboxRight, "Header 3");

		input = jq(".z-textbox").eq(0); // Header 2
		JQuery frozenInner = jq(".z-frozen-inner");
		frozenInner.scrollLeft(frozenInner.scrollLeft() + input.offsetLeft() + input.outerWidth() - (viewLeft + 40));
		waitResponse();
		inputLeft = input.offsetLeft();
		Assertions.assertTrue(inputLeft < viewLeft && inputLeft + input.outerWidth() > viewLeft,
				"the input should be partly behind the frozen column");
		clickVisiblePartAndCheck(input, viewLeft, listboxRight, "Header 2");
	}

	private void clickVisiblePartAndCheck(JQuery input, int viewLeft, int viewRight, String header) {
		JQuery body = jq(".z-listbox-body");
		int scrollLeft = body.scrollLeft();
		int inputLeft = input.offsetLeft();
		int x = (Math.max(inputLeft, viewLeft) + Math.min(inputLeft + input.outerWidth(), viewRight)) / 2;
		int y = input.offsetTop() + input.outerHeight() / 2;
		// move from the listbox body, since moving to the input itself would scroll it into view first
		getActions().moveToElement(toElement(body))
				.moveByOffset(x - (body.offsetLeft() + body.outerWidth() / 2), y - (body.offsetTop() + body.outerHeight() / 2))
				.click()
				.perform();
		waitResponse();

		Assertions.assertEquals(header, getZKLog(), "the click should reach the input");
		Assertions.assertTrue(input.is(":focus"), "the input should be focused");
		Assertions.assertEquals(scrollLeft, body.scrollLeft(), "the columns should not scroll");
		closeZKLog();
	}
}
