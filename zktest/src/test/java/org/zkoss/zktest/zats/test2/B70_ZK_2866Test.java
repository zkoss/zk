/* B70_ZK_2866Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 15:17:17 CST 2019, Created by rudyhuang

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
public class B70_ZK_2866Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery frozenBar = jq("@frozen .z-frozen-inner");
		int scrollLeftMax = frozenBar.scrollWidth() - frozenBar.width();
		frozenBar.scrollLeft(scrollLeftMax);
		waitResponse();
		frozenBar.scrollLeft(0);
		waitResponse();

		Assertions.assertEquals(
				jq("@column:contains(Apr)").offsetLeft(),
				jq("@auxheader:contains(Q2)").offsetLeft(), 2);
	}
}
