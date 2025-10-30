/* B95_ZK_4723Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 17 10:44:13 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4723Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		closeZKLog();
		click(jq("@button").eq(0));
		waitResponse();
		assertFalse(getZKLog().contains("Method 'loadValue' called with: null"));
		closeZKLog();
		click(jq("@button").eq(1));
		waitResponse();
		assertFalse(getZKLog().contains("Method 'loadValue' called with: null"));
	}
}
