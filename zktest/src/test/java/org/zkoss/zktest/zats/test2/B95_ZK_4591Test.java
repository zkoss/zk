/* B95_ZK_4591Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 13 10:17:09 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4591Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		click(widget("@tabbox").$n("right"));
		click(widget("@tabbox").$n("right"));
		click(widget("@tabbox").$n("right"));
		waitResponse();

		click(jq("@button"));
		waitResponse();
		assertNoJSError();
	}
}
