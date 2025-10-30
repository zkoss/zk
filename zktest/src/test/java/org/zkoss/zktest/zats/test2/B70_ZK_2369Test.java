/* B70_ZK_2369Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 17:44:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2369Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(2)"));
		waitResponse();
		click(jq("@button:eq(3)"));
		waitResponse();
		click(jq("@button:eq(4)"));
		waitResponse();

		assertNoJSError();
	}
}
