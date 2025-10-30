/* F50_2787713Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 15:51:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2787713Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(OK)"));
		waitResponse();
		Assertions.assertFalse(jq("@button:contains(OK)").is(":disabled"));
		Assertions.assertTrue(jq("@button:contains(Cancel)").is(":disabled"));

		click(jq("@button:contains(enable all)"));
		waitResponse();

		click(jq("@button:contains(Cancel)"));
		waitResponse();
		Assertions.assertTrue(jq("@button:contains(OK)").is(":disabled"));
		Assertions.assertFalse(jq("@button:contains(Cancel)").is(":disabled"));
	}
}
