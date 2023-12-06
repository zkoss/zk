/* B100_ZK_5037Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Nov 30 15:24:57 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B100_ZK_5037Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq(".z-listitem-checkable")); // if original bug exists then invisible checkmarks not clickable
		waitResponse();
		jq(".z-listitem-checkable").forEach(checkmark -> assertEquals(getCheckmarkColumnIndex(checkmark), 1));
		click(jq("@button"));
		waitResponse();
		jq(".z-listitem-checkable").forEach(checkmark -> assertEquals(getCheckmarkColumnIndex(checkmark), 0));
		click(jq("@button"));
		waitResponse();
		jq(".z-listitem-checkable").forEach(checkmark -> assertEquals(getCheckmarkColumnIndex(checkmark), 1));
	}

	private int getCheckmarkColumnIndex(JQuery checkmark) {
		int columnIndex;
		JQuery current = checkmark.parent().parent();
		for (columnIndex = 0; current.prev().exists() ; columnIndex++)
			current = current.prev();
		return columnIndex;
	}
}
