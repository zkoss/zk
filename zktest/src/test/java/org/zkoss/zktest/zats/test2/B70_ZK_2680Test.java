/* B70_ZK_2680Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 13 11:47:07 CST 2019, Created by rudyhuang

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
public class B70_ZK_2680Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery icon = jq(".z-nav:eq(0) > a");
		mouseOver(icon);
		waitResponse();

		click(jq(".z-nav-popup .z-nav-content").first());
		waitResponse(true);

		click(jq("body"));
		waitResponse();

		mouseOver(icon);
		waitResponse();
		Assertions.assertTrue(jq(".z-nav-popup .z-nav").hasClass("z-nav-open"));
	}
}
