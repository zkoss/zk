/* B36_2820527Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 28 18:03:56 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThan;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B36_2820527Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final JQuery col0 = jq("$col0");
		getActions().moveToElement(toElement(col0))
				.click(toElement(col0.toWidget().$n("btn")))
				.perform();
		waitResponse();

		click(jq("@menuitem[label=\"Author\"]"));
		waitResponse();
		MatcherAssert.assertThat(col0.width(), lessThan(1));

		final JQuery col4 = jq("$col4");
		getActions().moveToElement(toElement(col4))
				.click(toElement(col4.toWidget().$n("btn")))
				.perform();
		waitResponse();

		click(jq("@menuitem[label=\"Author\"]"));
		waitResponse();
		MatcherAssert.assertThat(col4.width(), lessThan(1));
	}
}
