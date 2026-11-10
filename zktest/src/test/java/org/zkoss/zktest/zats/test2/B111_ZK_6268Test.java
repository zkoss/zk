/* B111_ZK_6268Test.java

		Purpose:
				
		Description:
				
		History:
				Tue Nov 10 18:23:31 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B111_ZK_6268Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		final Widget tree = widget("@tree");
		final JQuery body = jq(tree.$n("body"));
		final JQuery rows = jq(tree.$n("cave"));
		assertEquals(220000, body.scrollTop());
		assertThat("rows end above the view", rows.positionTop() + rows.height(), greaterThanOrEqualTo(0));
		assertThat("rows start below the view", rows.positionTop(), lessThanOrEqualTo(body.height()));
	}
}
