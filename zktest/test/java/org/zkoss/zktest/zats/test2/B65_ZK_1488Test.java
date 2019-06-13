/* B65_ZK_1488Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 13 10:46:50 CST 2019, Created by rudyhuang

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
public class B65_ZK_1488Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery pp0 = jq(".z-menupopup:contains(context menu A)");
		JQuery pp1 = jq(".z-menupopup:contains(popup menu A)");

		rightClick(jq(".z-listitem:eq(0)"));
		waitResponse();
		Assert.assertTrue(pp0.isVisible());
		Assert.assertFalse(pp1.isVisible());

		click(jq(".z-listitem:eq(0)"));
		waitResponse();
		Assert.assertFalse(pp0.isVisible());
		Assert.assertTrue(pp1.isVisible());
	}
}
