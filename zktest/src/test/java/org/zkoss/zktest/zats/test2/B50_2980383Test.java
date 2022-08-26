/* B50_2980383Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 11:10:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2980383Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		Assertions.assertTrue(jq("@tab:last").hasClass("z-tab-selected"));
		JQuery tabs = jq("@tabs");
		Assertions.assertEquals(tabs.scrollWidth() - tabs.outerWidth(), tabs.scrollLeft(), 2);
	}
}
