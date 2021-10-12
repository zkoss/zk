/* B50_2980383Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 11:10:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2980383Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		Assert.assertTrue(jq("@tab:last").hasClass("z-tab-selected"));
		JQuery tabs = jq("@tabs");
		Assert.assertEquals(tabs.scrollWidth() - tabs.outerWidth(), tabs.scrollLeft(), 2);
	}
}
