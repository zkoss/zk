/* B95_ZK_4619Test.java

	Purpose:

	Description:

	History:
		Mon Jan 04 10:30:38 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4619Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery radio1 = jq("@radiogroup:eq(0)").find("@radio:eq(0)");
		JQuery radio2 = jq("@radiogroup:eq(1)").find("@radio:eq(0)");

		click(jq("@button"));
		waitResponse();
		click(radio1);
		click(radio2);
		Assertions.assertTrue(radio1.hasClass("z-radio-on"));
		Assertions.assertTrue(radio2.hasClass("z-radio-on"));
	}
}
