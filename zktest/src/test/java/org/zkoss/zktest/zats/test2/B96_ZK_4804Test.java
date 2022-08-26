/* B96_ZK_4804Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 12:51:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B96_ZK_4804Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		final Widget tree = widget("@tree");
		final JQuery treeBody = jq(tree.$n("body"));
		final int totalHeight = treeBody.scrollHeight();
		treeBody.scrollTop(totalHeight / 2); // scrolls to 50%
		waitResponse();

		click(jq("@button"));
		waitResponse();

		final JQuery content = jq(tree.$n("cave"));
		final int contentTop = content.positionTop();
		// Ensure you can see the content
		MatcherAssert.assertThat(contentTop, Matchers.lessThanOrEqualTo(0));
		MatcherAssert.assertThat(contentTop + content.height(), Matchers.greaterThanOrEqualTo(0));
	}
}
