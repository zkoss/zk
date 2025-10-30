/* B96_ZK_4909Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:43:15 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4909Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		mouseOver(jq("$nav1"));
		mouseOver(jq("$navitem2"));
		click(jq("$navitem2"));
		waitResponse();
		Assertions.assertFalse(jq(".z-nav-popup").exists(),
				"the navpopup should be killed");
	}

	@Test
	public void test2() {
		connect();
		mouseOver(jq("$nav1"));
		click(jq("$nav2"));
		waitResponse(true);
		click(jq("$navitem4"));
		waitResponse();
		Assertions.assertFalse(jq(".z-nav-popup").exists(),
				"the navpopup should be killed");
	}
}
