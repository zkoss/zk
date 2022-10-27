/* B85_ZK_3765Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 15 15:56:38 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
@Disabled
public class B85_ZK_3765Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		click(jq("@button").eq(1));
		waitResponse();
		JQuery msgbox = jq("@window");
		Assertions.assertNotNull(msgbox, "The message box not popped up.");
	}
}
