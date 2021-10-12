/* B50_3131458Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 14:50:25 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3131458Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		testIcon(jq(".z-button:eq(0)"), ".z-icon-caret-up");
		testIcon(jq(".z-button:eq(1)"), ".z-icon-caret-down");
	}

	private void testIcon(JQuery button, String cls) {
		click(button);
		waitResponse();
		Assert.assertTrue(jq(".z-listheader").find(cls).exists());
		Assert.assertTrue(jq(".z-column").find(cls).exists());
	}
}
