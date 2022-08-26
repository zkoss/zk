/* B86_ZK_4241Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 15 14:37:39 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4241Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		int offsetTop = jq("@popup").offsetTop();

		jq(".z-listbox-body").scrollTop(3000);
		waitResponse();

		Assertions.assertEquals(offsetTop, jq("@popup").offsetTop(), 1, "The position of popup is changed");
	}
}
