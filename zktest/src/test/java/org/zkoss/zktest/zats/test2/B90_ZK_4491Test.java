/* B90_ZK_4491Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 18 14:30:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4491Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		JQuery grid = jq("@grid");
		int width = grid.width();
		click(grid);
		JQuery body = grid.find(".z-grid-body");
		body.scrollTop(body.scrollHeight());
		waitResponse();
		assertThat(grid.width(), greaterThan(width));
	}
}
