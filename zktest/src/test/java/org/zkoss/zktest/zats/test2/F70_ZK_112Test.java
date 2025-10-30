/* F70_ZK_112Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 04 16:23:26 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_112Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery vsplitter = jq("$s2");
		dragdropTo(vsplitter, 0, 10, -200, 0);
		waitResponse();

		int oldWidth = jq(".z-vbox").width();
		dragdropTo(vsplitter, 0, 10, 30, 0);
		waitResponse();
		assertThat(jq(".z-vbox").width(), greaterThan(oldWidth));

		JQuery hsplitter = jq("$s1");
		int oldHeight = jq("@div:eq(0)").height();
		dragdropTo(hsplitter, 10, 0, 0, -100);
		waitResponse();
		assertThat(jq("@div:eq(0)").height(), lessThan(oldHeight));
	}
}
