/* B70_ZK_2990Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 17:38:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2990Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(jq("@notification").isVisible());

		click(widget("@window").$n("close"));
		waitResponse();
		click(widget("@notification").$n("cls"));
		waitResponse(true);
		Assert.assertFalse(jq("@notification").isVisible());
	}
}
