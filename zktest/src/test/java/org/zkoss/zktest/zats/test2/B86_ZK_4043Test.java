/* B86_ZK_4043Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 14 11:19:41 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4043Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(\"(A)\")"));
		waitResponse();
		click(jq("@button:contains(\"(B)\")"));
		waitResponse();
		click(jq("@button:contains(OK)"));
		waitResponse();
		click(jq("@button:contains(\"(C)\")"));
		waitResponse();
		click(jq("@button:contains(\"(A)\")"));
		waitResponse();
		click(jq("@button:contains(\"(B)\")"));
		waitResponse();

		Assertions.assertFalse(isZKLogAvailable(), "An exception was thrown.");
	}
}
