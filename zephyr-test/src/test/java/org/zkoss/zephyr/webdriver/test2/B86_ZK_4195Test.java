/* B86_ZK_4195Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 15:02:08 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4195Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery buttons = jq("@button");
		waitResponse();
		click(buttons.eq(0));
		waitResponse();
		click(buttons.eq(1));
		waitResponse();
		assertNoAnyError();
	}
}
