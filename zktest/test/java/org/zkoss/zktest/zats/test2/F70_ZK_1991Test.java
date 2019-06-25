/* F70_ZK_1991Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 11:44:18 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_1991Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Assert.assertEquals(2, driver.getWindowHandles().size());
	}
}
