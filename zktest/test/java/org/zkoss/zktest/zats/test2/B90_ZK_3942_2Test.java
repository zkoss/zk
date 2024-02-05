/* B90_ZK_3942Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 29 15:07:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_3942_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		Assert.assertEquals(jq("body").innerWidth() >> 1, jq("$div1").width(), 3);
	}
}