/* B86_ZK_4190Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 10:54:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertFalse("should have no error", isZKLogAvailable());

		closeZKLog();
		click(jq("@datebox:eq(1)"));
		waitResponse();
		click(jq("body"));
		waitResponse();
		Assert.assertFalse("should have no error", isZKLogAvailable());
	}
}
