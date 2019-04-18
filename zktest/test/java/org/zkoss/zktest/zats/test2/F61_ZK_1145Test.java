/* F61_ZK_1145Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 17:53:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F61_ZK_1145Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(jq("@notification").isVisible());

		click(widget("@notification").$n("cls"));
		waitResponse();
		Assert.assertFalse(jq("@notification").isVisible());
	}
}
