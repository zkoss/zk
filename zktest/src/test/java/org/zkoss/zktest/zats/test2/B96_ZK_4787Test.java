/* B96_ZK_4787Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 17 11:35:35 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4787Test extends WebDriverTestCase {
	@Test
	public void testServerPush() {
		connect();

		final JQuery page = jq("html");
		click(widget("$cbSP").$n("btn"));
		waitResponse(true);
		MatcherAssert.assertThat(page.scrollHeight(), lessThanOrEqualTo(page.innerHeight()));
	}

	@Test
	public void testNormal() {
		connect();

		final JQuery page = jq("html");
		click(widget("$cb").$n("btn"));
		waitResponse(true);
		MatcherAssert.assertThat(page.scrollHeight(), lessThanOrEqualTo(page.innerHeight()));
	}
}
