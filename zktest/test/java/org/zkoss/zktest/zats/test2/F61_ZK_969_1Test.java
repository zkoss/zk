/* F61_ZK_969_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 17:16:47 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F61_ZK_969_1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("Center", jq(".z-center-header").text());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("Center Label", jq(".z-center-body").text());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertTrue(hasError());
		click(jq(".z-messagebox-buttons .z-button"));
		waitResponse();

		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertEquals("CenterTest", jq(".z-center-header").text());
	}
}
