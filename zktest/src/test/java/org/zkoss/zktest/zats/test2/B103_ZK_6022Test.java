/* B103_ZK_6022Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 05 14:22:46 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class B103_ZK_6022Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn"));
		waitResponse();
		JQuery listbox = jq("@listbox");
		click(listbox);
		waitResponse();
		String tooltip = listbox.find(".z-icon-caret-up").attr("title");
		assertEquals("Ascending up (custom)", tooltip);
		click(listbox);
		waitResponse();
		tooltip = listbox.find(".z-icon-caret-down").attr("title");
		assertEquals("Descending down (custom)", tooltip);
	}
}
