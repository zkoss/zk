/* B85_ZK_3828Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 28 09:51:22 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3828Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		assertEquals("remove 'error' from this text", getTextbox().val());
		assertEquals("ERROR", getStatus().text());
		assertEquals("false", getFlag().text());

		type(getTextbox(), "correct value");
		waitResponse();
		assertEquals("OK", getStatus().text());
		assertEquals("true", getFlag().text());
	}

	private JQuery getTextbox() {
		return jq("@div > @textbox");
	}

	private JQuery getStatus() {
		return jq("@div > @label").eq(0);
	}

	private JQuery getFlag() {
		return jq("@div > @label").eq(1);
	}
}
