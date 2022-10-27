/* B86_ZK_4061Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 19 11:09:58 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4061Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();

		driver.navigate().back();
		waitResponse();

		Assertions.assertEquals("1", getZKLog());
	}
}
