/* B95_ZK_4410Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 08 11:36:37 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4410Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		assertNoAnyError();
	}
}
