/* B90_ZK_4344Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 30 14:30:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4344Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		click(jq("@button"));
		waitResponse();

		Assert.assertEquals("Tab 2", jq(".z-tab-selected").eq(0).text());
		Assert.assertEquals("Tab 2_2", jq(".z-tab-selected").eq(1).text());
	}
}
