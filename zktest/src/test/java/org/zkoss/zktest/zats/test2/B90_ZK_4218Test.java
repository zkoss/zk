/* B90_ZK_4218Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 24 15:43:50 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B90_ZK_4218Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();

		click(jq("@button:contains(Append Column)"));
		waitResponse();
		click(jq("@button:contains(Show all)"));
		waitResponse();

		checkWidths();
	}

	@Test
	public void test2() {
		connect();

		click(jq("@button:contains(Show all)"));
		waitResponse();
		click(jq("@button:contains(Append Column)"));
		waitResponse();

		checkWidths();
	}

	private void checkWidths() {
		final Widget grid = widget("@grid");
		int scrollbarWidth = Integer.parseInt(getEval("jq.scrollbarWidth()"));

		final int headerWidth = jq(grid.$n("headrows")).outerWidth();
		final int bodyWidth = jq(grid.$n("cave")).outerWidth();
		assertThat(headerWidth - bodyWidth, greaterThanOrEqualTo(scrollbarWidth));
	}
}
