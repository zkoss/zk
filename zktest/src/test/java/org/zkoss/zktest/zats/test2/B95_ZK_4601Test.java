/* B95_ZK_4601Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 15 16:56:27 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4601Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(Select Tab C)"));
		waitResponse(true);
		Assert.assertTrue("Content C invisible",
				jq("@tabpanel:eq(2) .z-tabpanel-content").isVisible());

		click(jq("@button:contains(Select Tab B)"));
		waitResponse(true);
		Assert.assertTrue("Content B invisible",
				jq("@tabpanel:eq(1) .z-tabpanel-content").isVisible());

		click(jq("$tabC"));
		waitResponse(true);
		Assert.assertTrue("Content C invisible",
				jq("@tabpanel:eq(2) .z-tabpanel-content").isVisible());
	}
}
