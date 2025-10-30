/* B70_ZK_2814Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 09 10:16:56 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2814Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		assertNoJSError();

		click(jq("@button:eq(1)"));
		waitResponse();
		assertNoJSError();
	}
}
