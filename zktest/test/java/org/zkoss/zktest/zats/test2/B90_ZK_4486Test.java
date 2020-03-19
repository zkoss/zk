/* B90_ZK_4486Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 18 17:00:29 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B90_ZK_4486Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		JQuery listheader = jq("@listheader:eq(0)");
		// CSS flex: display: none
		// JS flex: width 0
		if (listheader.isVisible())
			Assert.assertEquals("listheader is not hidden", 0, listheader.width());
	}
}
