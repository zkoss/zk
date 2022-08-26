/* B86_ZK_4067Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 28 10:43:31 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4067Test extends WebDriverTestCase {
	private static final String SORT_ICON = ".z-icon-caret-up";

	@Test
	public void test() {
		connect();
		testSort(jq(".z-column"), 0);
		testSort(jq(".z-listheader"), 1);
		testSort(jq(".z-treecol"), 2);
	}

	private void testSort(JQuery header, int index) {
		JQuery button = jq(".z-button").eq(index);
		click(button);
		waitResponse();

		click(header);
		waitResponse();
		Assertions.assertTrue(header.find(SORT_ICON).exists());

		click(button);
		waitResponse();
		Assertions.assertFalse(header.find(SORT_ICON).exists());
	}
}
