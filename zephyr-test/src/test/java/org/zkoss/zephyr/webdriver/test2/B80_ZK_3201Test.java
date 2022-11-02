/* B80_ZK_3201Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Sep  8, 2016  4:32:29 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author Sefi
 */
public class B80_ZK_3201Test extends ClientBindTestCase {
	@Test
	public void test() {
		try {
			connect();
			sleep(2000);
			click(jq("$btn"));
			waitResponse();
		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
		assertTrue(true);
	}
}
