/* B95_ZK_4717Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 14 16:08:41 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4717Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(create)"));
		waitResponse();
		Assertions.assertTrue(jq("@window").isVisible());
		click(widget("@window").$n("close"));
		waitResponse();

		click(jq("@button:contains(fileupload)"));
		waitResponse();
		click(jq(".z-window .z-button:contains(Upload)"));
		waitResponse();
		hasError();
		click(jq("@button:last"));
		waitResponse();

		click(jq("@button:contains(create)"));
		waitResponse();
		Assertions.assertTrue(jq("@window").isVisible());
	}
}
