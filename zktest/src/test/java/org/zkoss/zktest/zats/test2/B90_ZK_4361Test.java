/* B90_ZK_4491Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 18 14:30:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4361Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery col = jq(".z-south-collapsed");
		JQuery showBtn = jq("$sBtn");
		JQuery hideBtn = jq("$hBtn");
		//case 1
		click(jq(".z-borderlayout-icon"));
		waitResponse();
		click(hideBtn);
		waitResponse();
		click(col.find(".z-borderlayout-icon"));
		waitResponse();
		Assertions.assertFalse(jq("@window").isVisible());

		//case 2
		click(showBtn);
		waitResponse();
		click(hideBtn);
		waitResponse();
		click(jq(".z-borderlayout-icon"));
		waitResponse();
		click(showBtn);
		waitResponse();
		click(col.find(".z-borderlayout-icon"));
		waitResponse();
		Assertions.assertTrue(jq("@window").isVisible());
	}
}
