/* B86_ZK_4144Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 21 16:33:18 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4144Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		JQuery errbox = jq("@errorbox:eq(1)");
		int originalTop = errbox.offsetTop();

		jq("@groupbox > .z-groupbox-content").scrollTop(50);
		waitResponse();

		Assertions.assertNotEquals(originalTop, errbox.offsetTop(), "Should follow scroll");
	}
}
