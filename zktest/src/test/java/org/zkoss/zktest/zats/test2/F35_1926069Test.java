/* F35_1926069Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 14 11:22:59 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F35_1926069Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery inf2 = jq("$inf2");
		click(jq("@bandbox"));
		waitResponse();
		sendKeys(jq("@bandbox").toWidget().$n("real"), "abc");
		waitResponse();
		click(jq(".z-bandbox").toWidget().$n("btn"));
		waitResponse();
		Assertions.assertEquals("abc,,true", inf2.text());

		click(jq("@combobox"));
		waitResponse();
		sendKeys(jq("@combobox").toWidget().$n("real"), "xyz");
		waitResponse();
		click(jq(".z-combobox").toWidget().$n("btn"));
		waitResponse();
		Assertions.assertEquals("xyz,,true", inf2.text());
	}
}
