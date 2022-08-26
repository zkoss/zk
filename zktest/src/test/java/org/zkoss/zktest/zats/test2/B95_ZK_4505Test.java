/* B95_ZK_4505Test.java

	Purpose:

	Description:

	History:
		Mon Dec 14 16:00:21 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;



/**
 * @author katherinelin
 */
public class B95_ZK_4505Test extends WebDriverTestCase {
	@Test
	public void test() throws InterruptedException {
		connect();
		JQuery btn = jq("@button").eq(0);
		click(btn);
		waitResponse();
		assertEquals(btn.outerHeight(), jq(".z-groupbox-content").eq(0).height());
	}
}