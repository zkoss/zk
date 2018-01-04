/* B85_ZK_3625Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 04 16:41:27 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3625Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Assert.assertFalse(
			"The scroll control appears!",
			jq("@menubar .z-menubar-left").isVisible());
	}
}
