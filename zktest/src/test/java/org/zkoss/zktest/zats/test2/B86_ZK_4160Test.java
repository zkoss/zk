/* B86_ZK_4160Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 14 18:08:21 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4160Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@tab:last > .z-tab-content"));
		waitResponse();
		click(jq("@button:last"));
		click(jq("@button:last"));
		click(jq("@button:last"));
		waitResponse();

		click(jq("@tab:first > .z-tab-content"));
		waitResponse();
		click(jq("@button:first"));
		click(jq("@button:first"));
		click(jq("@button:first"));
		waitResponse();

		click(jq("@tab:last > .z-tab-content"));
		waitResponse();
		Assert.assertEquals(1 + jq("$treeitems > @label").length(), jq("@treerow").length());
	}
}
