/* B86_ZK_4223Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 04 14:45:27 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4223Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery div1 = jq(".z-div").eq(0);
		JQuery div2 = jq(".z-div").eq(1);
		Assert.assertEquals(div1.width(), div1.find(".z-grid").width(), 2);
		Assert.assertEquals(div2.width(), div2.find(".z-grid").width(), 2);
	}
}
