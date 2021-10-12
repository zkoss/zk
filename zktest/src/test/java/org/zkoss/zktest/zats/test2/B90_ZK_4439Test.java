/* B90_ZK_4439Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 03 17:19:55 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4439Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-widget button:eq(0)"));
		waitResponse();
		click(jq(".z-widget button:eq(1)"));
		waitResponse();

		String[] lines = getZKLog().split("\n");
		Assert.assertNotEquals(lines[0], lines[1]);
	}
}
