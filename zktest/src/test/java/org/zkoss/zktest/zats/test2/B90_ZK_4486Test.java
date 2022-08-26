/* B90_ZK_4486Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 18 17:00:29 CST 2020, Created by rudyhuang

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
public class B90_ZK_4486Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		JQuery listheader = jq("@listheader:eq(0)");
		// CSS flex: display: none
		// JS flex: width 0
		if (listheader.isVisible())
			Assertions.assertEquals(0, listheader.width(), "listheader is not hidden");
	}
}
