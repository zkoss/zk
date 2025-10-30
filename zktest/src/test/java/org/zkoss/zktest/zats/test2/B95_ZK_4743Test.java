/* B95_ZK_4743Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Dec 11 10:29:03 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4743Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int columnWidth = jq(".z-listheader").outerWidth();

		jq(".z-frozen-inner").scrollLeft(8 * columnWidth + 120); // Scroll right until items 10, 11, 12 are in view
		waitResponse();

		clickTextboxAndCheck(9);

		clickTextboxAndCheck(10);

		clickTextboxAndCheck(11);

	}

	private void clickTextboxAndCheck(int textboxIndex) {
		click(jq("@textbox").eq(textboxIndex));
		waitResponse();
		Assertions.assertEquals(jq(".z-listbox-header").scrollLeft(), jq(".z-listbox-body").scrollLeft(), "head and body should stay sync.");
		Assertions.assertEquals(jq(".z-listbox-body").scrollLeft(), jq(".z-frozen-inner").scrollLeft(), "scrollbar position and view position should stay sync.");
	}
}
