/* B95_ZK_4263Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 15 15:27:42 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B95_ZK_4263_gridTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		final JQuery group = jq("@group:eq(0)");
		click(widget(group).$n("img"));
		waitResponse();
		Assert.assertFalse("The group is still opened", group.hasClass("z-group-open"));

		click(widget(group).$n("img"));
		waitResponse();
		Assert.assertTrue("The group is still opened", group.hasClass("z-group-open"));
	}
}
