/* B70_ZK_2929Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 12:34:56 CST 2019, Created by rudyhuang

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
public class B70_ZK_2929Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery popup = jq(".z-combobox-popup.z-combobox-open");
		click(widget("@combobox:eq(0)").$n("btn"));
		waitResponse(true);
		Assertions.assertEquals(popup.width(), popup.scrollWidth(), 1);

		click(widget("@combobox:eq(1)").$n("btn"));
		waitResponse(true);
		Assertions.assertEquals(popup.width(), popup.scrollWidth(), 1);
	}
}
