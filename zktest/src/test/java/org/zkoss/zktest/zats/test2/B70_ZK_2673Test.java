/* B70_ZK_2673Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 13 11:21:14 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2673Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		mouseOver(jq(".z-nav-content"));
		waitResponse(true);
		JQuery navpp = jq(".z-nav-popup .z-nav-content");
		click(navpp.eq(0));
		waitResponse(true);
		click(navpp.eq(1));
		waitResponse(true);
		int origWidth = jq(".z-nav-popup").width();
		click(navpp.eq(2));
		waitResponse(true);
		assertThat(jq(".z-nav-popup").width(), greaterThan(origWidth));
	}
}
