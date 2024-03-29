/* B96_ZK_4955Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 13 14:22:31 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */

public class B96_ZK_4955Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqComboInput = jq("@combobox input");
		sendKeys(jqComboInput, "0");
		waitResponse();
		blur(jqComboInput);
		waitResponse();
		assertEquals("Onchange: selection -> 1", getZKLog());
		closeZKLog();
		click(jq("@button"));
		waitResponse();
		assertEquals("selection count: 1", getZKLog());
	}
}
