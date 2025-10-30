/* F50_3052857Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 16:21:51 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3052857Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testCombobox(jq("@combobox:eq(0)"));
		testCombobox(jq("@combobox:eq(1)"));
		testCombobox(jq("@combobox:eq(2)"));
	}

	private void testCombobox(JQuery combo) {
		JQuery input = combo.find("input");
		click(input);
		waitResponse();
		sendKeys(input, "opt");
		waitResponse();
		JQuery options = jq(".z-combobox-popup.z-combobox-open").find("@comboitem");
		Assertions.assertEquals(15, options.length());
		sendKeys(input, Keys.ESCAPE);
	}
}
