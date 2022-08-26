/* B86_ZK_4054Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 10 18:19:15 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4054Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		clickOpenButtons();
		Assertions.assertEquals("false\nfalse\nfalse", getZKLog());

		click(jq("@button:contains(Toggle visible)"));
		waitResponse();

		closeZKLog();
		clickOpenButtons();
		Assertions.assertEquals("true\ntrue\ntrue", getZKLog());

		click(jq("@button:contains(Toggle visible)"));
		waitResponse();

		closeZKLog();
		clickOpenButtons();
		Assertions.assertEquals("false\nfalse\nfalse", getZKLog());
	}

	private void clickOpenButtons() {
		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(2)"));
		waitResponse();
	}
}
