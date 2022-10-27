/* B80_ZK_2787Test.java

	Purpose:
		
	Description:
		
	History:
		5:30 PM 1/5/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2787Test extends ClientBindTestCase {
	@Test
	public void testZK2787() {
		connect();
		assertNoAnyError();
	}
}
