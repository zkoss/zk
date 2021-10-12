/* F70_ZK_2495_2Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 17:09:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_2495_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sleep(5000); // wait for crash
		Assert.assertEquals("Ooooops!! ErrorCode: 2", jq("body").text());
	}
}
