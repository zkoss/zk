/* F50_2931798Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 17:08:29 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2931798Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertTrue(jq(".z-apply-mask").isVisible());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertFalse(jq(".z-apply-mask").isVisible());

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertFalse(jq(".z-apply-mask").isVisible());

		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertTrue(jq(".z-apply-mask").isVisible());

		click(jq("@button:last"));
		waitResponse();
		Assert.assertFalse(jq(".z-apply-mask").isVisible());
	}
}
