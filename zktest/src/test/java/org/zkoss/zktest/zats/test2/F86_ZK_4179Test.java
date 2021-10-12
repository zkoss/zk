/* F86_ZK_4179Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 24 17:34:48 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4179Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertFalse("JavaScript exception", isZKLogAvailable());
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertFalse("JavaScript exception", isZKLogAvailable());

		driver.navigate().back();
		waitResponse();
		Assert.assertTrue("ZK onHistoryPopState error?", isZKLogAvailable());
		closeZKLog();

		driver.navigate().forward();
		waitResponse();
		Assert.assertTrue("ZK onHistoryPopState error?", isZKLogAvailable());
	}
}
