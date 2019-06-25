/* B50_3297378Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:11:13 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3297378Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery buttons = jq(".z-button");
		click(buttons.eq(0));
		waitResponse();
		Assert.assertFalse(buttons.eq(0).is(":disabled"));
		Assert.assertFalse(buttons.eq(1).is(":disabled"));
		Assert.assertTrue(buttons.eq(2).is(":disabled"));
	}
}
