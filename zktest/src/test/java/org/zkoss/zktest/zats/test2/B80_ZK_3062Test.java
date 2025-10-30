/* B80_ZK_3062Test.java

	Purpose:
		
	Description:
		
	History:
		12:11 PM 1/11/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_3062Test extends WebDriverTestCase {
	@Test
	public void testZK3062() {
		connect();
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
