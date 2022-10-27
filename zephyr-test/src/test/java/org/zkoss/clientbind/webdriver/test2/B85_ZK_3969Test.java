/* B85_ZK_3969Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 26 11:34:31 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3969Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		assertNoJSError();
	}
}
