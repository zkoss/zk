/* B86_ZK_4076Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 19 17:21:18 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4076Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("test ok", getZKLog().trim());

		closeZKLog();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("test ok", getZKLog().trim());
	}
}
