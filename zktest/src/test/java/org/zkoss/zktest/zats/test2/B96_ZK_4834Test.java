/* B96_ZK_4834Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 12 16:31:06 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B96_ZK_4834Test extends WebDriverTestCase {
	@Test
	public void testMethod1() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();

		final Widget tree = widget("@tree");
		final JQuery body = jq(tree.$n("body"));
		Assertions.assertEquals(240000, body.scrollTop());
		testItemInViewport(body, jq(tree.$n("cave")));
	}

	private void testItemInViewport(JQuery scrollBody, JQuery item) {
		final int top = item.positionTop();
		if (top >= 0) {
			int viewportHeight = scrollBody.height();
			MatcherAssert.assertThat("Too far from the bottom", top, lessThanOrEqualTo(viewportHeight));
		} else {
			MatcherAssert.assertThat("Too far from the top", top, greaterThanOrEqualTo(-item.height()));
		}
	}

	@Test
	public void testMethod2() {
		connect();

		click(jq("@button:eq(1)"));
		sleep(2000L);
		waitResponse();

		final Widget tree = widget("@tree");
		final JQuery body = jq(tree.$n("body"));
		Assertions.assertEquals(220000, body.scrollTop());
		testItemInViewport(body, jq(tree.$n("cave")));
	}
}
