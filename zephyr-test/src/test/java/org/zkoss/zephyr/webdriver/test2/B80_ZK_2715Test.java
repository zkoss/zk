/* B80_ZK_2715Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Aug 18, 2016  6:16:01 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author Sefi
 */
public class B80_ZK_2715Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery button = jq("@button");
		JQuery tab = jq("@tab").eq(0);
		Assertions.assertEquals("tab2", tab.find(".z-tab-text").text());
		String uuid = jq("@tab").get(1).get("id");
		click(button);
		waitResponse();
		tab = jq("@tab").eq(1);
		Assertions.assertEquals(tab.find(".z-tab-text").text(), "tab3");
		Assertions.assertNotSame(uuid, tab.get(0).get("id"));
	}
}
