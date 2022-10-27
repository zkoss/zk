/* B90_ZK_4526Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 12 11:51:58 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class B90_ZK_4526Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		Assertions.assertEquals("init VM2", getZKLog());
	}
}
