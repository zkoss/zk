/* B80_ZK_3059Test.java

	Purpose:
		
	Description:
		
	History:
		11:39 AM 1/11/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_3059Test extends WebDriverTestCase {
	@Test
	public void testZK3059() {
		connect();
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
