/* F50_3287044Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 12:36:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3287044Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals("2,000.02", jq("@doublebox:eq(0)").val());
		Assertions.assertEquals("2,000.02", jq("@decimalbox:eq(0)").val());
		Assertions.assertEquals("2,000.02", jq("@doublespinner:eq(0) input").val());

		Assertions.assertEquals("2\u00A0000,02", jq("@doublebox:eq(1)").val());
		Assertions.assertEquals("2\u00A0000,02", jq("@decimalbox:eq(1)").val());
		Assertions.assertEquals("2\u00A0000,02", jq("@doublespinner:eq(1) input").val());

		Assertions.assertEquals("2.000,02", jq("@doublebox:eq(2)").val());
		Assertions.assertEquals("2.000,02", jq("@decimalbox:eq(2)").val());
		Assertions.assertEquals("2.000,02", jq("@doublespinner:eq(2) input").val());

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("2,000.02", jq("@doublebox:eq(0)").val());
		Assertions.assertEquals("2,000.02", jq("@decimalbox:eq(0)").val());
		Assertions.assertEquals("2,000.02", jq("@doublespinner:eq(0) input").val());
		Assertions.assertEquals("2,000.02", jq("@doublebox:eq(1)").val());
		Assertions.assertEquals("2,000.02", jq("@decimalbox:eq(1)").val());
		Assertions.assertEquals("2,000.02", jq("@doublespinner:eq(1) input").val());
		Assertions.assertEquals("2,000.02", jq("@doublebox:eq(2)").val());
		Assertions.assertEquals("2,000.02", jq("@decimalbox:eq(2)").val());
		Assertions.assertEquals("2,000.02", jq("@doublespinner:eq(2) input").val());
	}
}
