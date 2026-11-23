/* F90_ZK_4372Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 16 18:09:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_4372Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertTrue(jq("@notification").exists());

		click(jq("$btnLoseFocus"));
		waitResponse(true);
		Assert.assertFalse(jq("@notification").exists());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertTrue(jq("@notification").exists());
	}
}
