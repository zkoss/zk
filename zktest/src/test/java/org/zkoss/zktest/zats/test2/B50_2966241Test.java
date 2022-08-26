/* B50_2966241Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 10:12:44 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2966241Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals("OrderName - 3", jq("@combobox input").val());

		click(widget("@combobox").$n("btn"));
		waitResponse();
		click(jq("@comboitem:eq(7)"));
		waitResponse();
		click(widget("@combobox").$n("btn"));
		waitResponse();
		Assertions.assertTrue(jq("@comboitem:eq(7)").hasClass("z-comboitem-selected"));
		Assertions.assertEquals("OrderName\u00A0-\u00A03", jq("@comboitem:eq(3)").text());
	}
}
