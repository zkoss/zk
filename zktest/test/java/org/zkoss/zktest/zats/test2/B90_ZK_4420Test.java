/* B90_ZK_4420Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 26 11:40:19 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B90_ZK_4420Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();

		final int igBottomPos = calculateBottomPos(jq("@inputgroup"));
		verifyLabelPositionBottom(widget("@checkbox:eq(0)"), igBottomPos);
		verifyLabelPositionBottom(widget("@checkbox:eq(1)"), igBottomPos);
		verifyLabelPositionBottom(widget("@checkbox:eq(2)"), igBottomPos);
	}

	private int calculateBottomPos(JQuery wgt) {
		return wgt.positionTop() + wgt.height();
	}

	private void verifyLabelPositionBottom(Widget checkbox, int bottomPos) {
		int diff = bottomPos - calculateBottomPos(jq(checkbox.$n("cnt")));
		Assert.assertThat("The label was so close to bottom line", diff, not(lessThanOrEqualTo(5)));
	}
}
