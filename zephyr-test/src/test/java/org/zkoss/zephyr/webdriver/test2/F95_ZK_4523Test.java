/* F95_ZK_4523Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 06 17:03:21 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F95_ZK_4523Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery buttons = jq("@button");
		click(buttons.eq(0));
		waitResponse();
		assertEquals("Greetings, John Smith!", getZKLog());
		closeZKLog();
		waitResponse();
		click(buttons.eq(1));
		waitResponse();
		assertEquals("Greetings, John Smith!", getZKLog());
	}
}
