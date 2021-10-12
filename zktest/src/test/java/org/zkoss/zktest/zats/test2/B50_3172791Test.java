/* B50_3172791Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 18:18:35 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3172791Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertTrue(getFontSize(jq(".z-button:eq(0)")) > getFontSize(jq(".z-button:eq(1)")));
	}

	private int getFontSize(JQuery target) {
		return Integer.valueOf(target.css("font-size").replace("px", ""));
	}
}
