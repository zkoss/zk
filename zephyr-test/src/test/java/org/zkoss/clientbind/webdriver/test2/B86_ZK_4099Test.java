/* B86_ZK_4099Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 09:47:47 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4099Test extends ClientBindTestCase {
	@Test
	public void test() {
		// Test FormProxyHandler
		connect();
		sleep(2000);

		// Test ViewModelProxyHandler
		click(jq("@button"));
		waitResponse();
		assertNoAnyError();
	}
}
