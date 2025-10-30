/* F86_ZK_4294Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 28 14:25:51 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4294Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox:eq(1)"));
		waitResponse();

		Assertions.assertTrue(jq("@textbox:eq(1)").is(":focus"));
	}
}
