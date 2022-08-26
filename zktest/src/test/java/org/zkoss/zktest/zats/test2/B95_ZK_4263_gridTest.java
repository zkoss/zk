/* B95_ZK_4263Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 15 15:27:42 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

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
		Assertions.assertFalse(group.hasClass("z-group-open"),
				"The group is still opened");

		click(widget(group).$n("img"));
		waitResponse();
		Assertions.assertTrue(group.hasClass("z-group-open"),
				"The group is still opened");
	}
}
