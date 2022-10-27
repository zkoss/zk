/* B95_ZK_4515Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 28 12:41:09 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4515Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@toolbarbutton").eq(0));
		waitResponse();
		type(jq("@textbox").eq(1), "Sun");
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();

		Assertions.assertTrue(jq("@button").is(":disabled"), "The button should be disabled");
	}
}
