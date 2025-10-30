/* F50_3030481Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 12:06:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3030481Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@combobox").$n("btn"));
		waitResponse();
		Assertions.assertEquals("David", jq("@combobox input").val());
		Assertions.assertEquals("David", jq(".z-combobox-popup.z-combobox-open").find(".z-comboitem-selected").text());
	}
}
