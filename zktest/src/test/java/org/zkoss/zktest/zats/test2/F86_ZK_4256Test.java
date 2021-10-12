/* F86_ZK_4256Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 15 16:52:04 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4256Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(Start)"));
		waitResponse();
		click(jq("@button:contains(Abort)"));
		waitResponse();

		final String currentValue = jq("$time").text();
		sleep(2000);
		Assert.assertNotEquals(currentValue, jq("$time").text());
	}
}
