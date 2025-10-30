/* B86_ZK_4190Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 10:54:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4190Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@datebox:eq(0)"));
		waitResponse();
		click(jq("body"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "should have no error");

		closeZKLog();
		click(jq("@datebox:eq(1)"));
		waitResponse();
		click(jq("body"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "should have no error");
	}
}
