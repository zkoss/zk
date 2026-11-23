/* B50_3180824Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 18:41:58 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3180824Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery select = jq(".z-select");
		JQuery option = jq(".z-option:contains(A)");
		int scrollTop = select.scrollTop();

		click(option);
		waitResponse();

		Assert.assertTrue(option.is(":selected"));
		Assert.assertEquals(scrollTop, select.scrollTop());
	}
}
