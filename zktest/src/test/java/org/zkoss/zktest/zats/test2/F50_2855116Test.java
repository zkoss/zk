/* F50_2855116Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 16:37:55 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2855116Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		for (int i = 0; i < 5; i++) {
			handleColumn(i);
		}
	}

	private void handleColumn(int index) {
		String expectedValue = jq(String.format("@listitem:eq(%d) @listcell:eq(1)", index)).text();
		try {
			click(jq(String.format("@listitem:eq(%d) @button:first", index)));
			waitResponse();
			Assertions.assertEquals(expectedValue, getMessageBoxContent());
		} finally {
			click(jq(".z-messagebox-button:contains(OK)"));
			waitResponse();
		}
		try {
			click(jq(String.format("@listitem:eq(%d) @button:last", index)));
			waitResponse();
			Assertions.assertEquals(expectedValue, getMessageBoxContent());
		} finally {
			click(jq(".z-messagebox-button:contains(OK)"));
			waitResponse();
		}
	}
}
