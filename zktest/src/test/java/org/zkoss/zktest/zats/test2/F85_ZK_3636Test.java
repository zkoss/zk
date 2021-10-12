/* F85_ZK_3636Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 09:41:12 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F85_ZK_3636Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery stack = jq(".z-button .z-icon-stack");
		Assert.assertTrue(stack.find(".z-icon-home").exists());
		Assert.assertTrue(stack.find(".z-icon-check").exists());
	}
}
