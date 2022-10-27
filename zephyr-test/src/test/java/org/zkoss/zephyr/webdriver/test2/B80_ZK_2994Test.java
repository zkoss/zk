/* B80_ZK_2994Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 29 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_2994Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").get(0));
		waitResponse();
		assertTrue(!jq(".z-messagebox").exists());
	}
}
