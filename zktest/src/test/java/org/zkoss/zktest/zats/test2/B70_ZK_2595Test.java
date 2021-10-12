/* B70_ZK_2595Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 15 10:05:08 CST 2020, Created by rudyhuang

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
public class B70_ZK_2595Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final JQuery blacks = jq(".black");
		int widthB1 = blacks.eq(0).width();
		int widthB2 = blacks.eq(1).width();
		int heightB3 = blacks.eq(2).height();
		int heightB4 = blacks.eq(3).height();

		click(jq("@button"));
		waitResponse();

		MatcherAssert.assertThat(blacks.eq(0).width(), lessThan(widthB1));
		MatcherAssert.assertThat(blacks.eq(1).width(), lessThan(widthB2));
		MatcherAssert.assertThat(blacks.eq(2).height(), lessThan(heightB3));
		MatcherAssert.assertThat(blacks.eq(3).height(), lessThan(heightB4));
	}
}
