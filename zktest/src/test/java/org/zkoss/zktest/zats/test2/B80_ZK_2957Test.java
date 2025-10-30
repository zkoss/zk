/* B80_ZK_2957Test.java

	Purpose:
		
	Description:
		
	History:
		10:29 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2957Test extends WebDriverTestCase {
	@Test
	public void testZK2957() {
		connect();
		waitResponse(); // zk.log will show in a few ms later.
		assertEquals("data-handler works", getZKLog());
	}
}
