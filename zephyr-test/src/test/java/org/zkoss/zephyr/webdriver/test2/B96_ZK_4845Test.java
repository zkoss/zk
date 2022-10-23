/* B96_ZK_4800Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 12 15:03:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4845Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		click(jq("@button"));
		waitResponse();
		assertEquals("Second", jq("$l1").text());
		assertEquals("Second", jq("$l2").text());
	}
}
