/* B86_ZK_4016Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 22 12:36:23 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4016Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertTrue(getZKLog().length() > 0);
	}
}
