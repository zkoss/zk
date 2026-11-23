/* B60_ZK_866Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 25 16:15:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B60_ZK_866Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(2)"));
		waitResponse();

		Assert.assertEquals("[1]", jq("$msg").text());
		Assert.assertEquals("1", jq(".z-listitem-selected").eval("index()"));
	}
}
