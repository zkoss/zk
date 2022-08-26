/* F60_ZK_701_GridTest.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 12:21:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F60_ZK_701_GridTest extends WebDriverTestCase {
	@Test
	public void testClone() {
		connect();

		click(widget("@group").$n("img"));
		waitResponse();
		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertFalse(hasError());

		click(widget("@group").$n("img"));
		waitResponse();
		Assertions.assertTrue(jq("@group:first").hasClass("z-group-open"));
	}

	@Test
	public void testCloneSerialize() {
		connect();

		click(widget("@group").$n("img"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertFalse(hasError());

		click(widget("@group").$n("img"));
		waitResponse();
		Assertions.assertTrue(jq("@group:first").hasClass("z-group-open"));
	}
}
