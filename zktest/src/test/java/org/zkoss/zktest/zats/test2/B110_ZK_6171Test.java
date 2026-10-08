/* B110_ZK_6171Test.java

	Purpose:

	Description:

	History:
		Thu Oct 08 15:40:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6171Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int listboxLeft = jq(".z-listbox").offsetLeft();
		int listboxRight = listboxLeft + jq(".z-listbox-body").outerWidth();
		int viewLeft = listboxLeft + jq(".z-listcell").eq(0).outerWidth();
		JQuery serverInput = jq(".z-textbox").eq(0); // Header 4
		JQuery clientInput = jq(".z-textbox").eq(1); // Header 5

		// scroll until the inputs are past the frozen column, without reaching the end
		jq(".z-frozen-inner").scrollLeft(serverInput.offsetLeft() - viewLeft - 100);
		waitResponse();
		Assertions.assertTrue(serverInput.offsetLeft() > viewLeft
				&& clientInput.offsetLeft() + clientInput.outerWidth() < listboxRight, "the inputs should be in view");

		clickAndCheck(serverInput);
		clickAndCheck(clientInput);
	}

	private void clickAndCheck(JQuery input) {
		int scrollLeft = jq(".z-listbox-body").scrollLeft();
		int frozenLeft = jq(".z-listcell").eq(0).offsetLeft();
		click(input);
		waitResponse();

		Assertions.assertTrue(input.is(":focus"), "the input should keep the focus");
		Assertions.assertEquals(scrollLeft, jq(".z-listbox-body").scrollLeft(), "the columns should not move");
		Assertions.assertEquals(scrollLeft, jq(".z-listbox-header").scrollLeft(), "head and body should stay sync");
		Assertions.assertEquals(scrollLeft, jq(".z-frozen-inner").scrollLeft(), "the scrollbar should stay with the columns");
		Assertions.assertEquals(frozenLeft, jq(".z-listcell").eq(0).offsetLeft(), "the frozen column should stay in place");
	}
}
