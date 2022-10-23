/* B96_ZK_4855Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 20 15:50:21 CST 2021, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4855Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() throws Exception {
		connect();
		click(jq("$btn1"));
		waitResponse();
		assertEquals(2, jq("@listitem").length());
		click(jq("$btn2"));
		waitResponse();
		assertEquals(4, jq("@listitem").length());
	}
}
