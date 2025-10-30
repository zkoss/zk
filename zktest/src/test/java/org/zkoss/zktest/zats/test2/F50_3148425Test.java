/* F50_3148425Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 17:43:34 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3148425Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals("Fruits", jq("@group").eq(0).find("@label").text());
		Assertions.assertEquals("Grains", jq("@group").eq(1).find("@label").text());
		Assertions.assertEquals("Poultry & Lean Meats", jq("@group").eq(2).find("@label").text());
		Assertions.assertEquals("Seafood", jq("@group").eq(3).find("@label").text());
		Assertions.assertEquals("Vegetables", jq("@group").eq(4).find("@label").text());
	}
}
