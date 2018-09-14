/* B86_ZK_4043Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 14 11:19:41 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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

		Assert.assertFalse("An exception was thrown.", isZKLogAvailable());
	}
}
