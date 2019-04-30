/* B86_ZK_4275Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 30 16:01:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4275Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		sleep(1000); // wait for server push
		Assert.assertEquals(2, jq("@button").length());

		closeZKLog();
		click(jq("@button:last"));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
		Assert.assertEquals("did a click", getZKLog());
	}
}
