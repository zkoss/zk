/* B90_ZK_4376Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 21 15:29:38 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B90_ZK_4376Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final JQuery badge1 = jq(widget("@nav:eq(0)").$n("info"));
		final JQuery badge2 = jq(widget("@nav:eq(1)").$n("info"));
		final String oldBadge1Text = badge1.text();
		final String oldBadge2Text = badge2.text();

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(oldBadge1Text, badge1.text());
		Assert.assertEquals(oldBadge2Text, badge2.text());
	}
}
