/* B70_ZK_2686Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 10:10:08 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2686Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		click(jq("@tab:eq(1)"));
		waitResponse();
		Assertions.assertNotEquals(0, jq(".z-west:visible").width());

		click(jq("@tab:eq(2)"));
		waitResponse();
		Assertions.assertNotEquals(0, jq(".z-west:visible").width());
	}
}
