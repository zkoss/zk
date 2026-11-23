/* F50_3299209Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 15:22:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3299209Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@menuitem:eq(0)"));
		Assert.assertTrue(jq("@menuitem:eq(0) a").is("[disabled]"));
		waitResponse();
		Assert.assertFalse(jq("@menuitem:eq(0) a").is("[disabled]"));

		click(jq("@menuitem:eq(1)"));
		Assert.assertTrue(jq("@menuitem:eq(1) a").is("[disabled]"));
		Assert.assertTrue(jq("@menuitem:eq(2) a").is("[disabled]"));
		waitResponse();
		Assert.assertFalse(jq("@menuitem:eq(1) a").is("[disabled]"));
		Assert.assertTrue(jq("@menuitem:eq(2) a").is("[disabled]"));

		click(jq("@menuitem:eq(3)"));
		waitResponse();
		click(jq("@menuitem:eq(2)"));
		Assert.assertTrue(jq("@menuitem:eq(1) a").is("[disabled]"));
		Assert.assertTrue(jq("@menuitem:eq(2) a").is("[disabled]"));
		waitResponse();
		Assert.assertTrue(jq("@menuitem:eq(1) a").is("[disabled]"));
		Assert.assertFalse(jq("@menuitem:eq(2) a").is("[disabled]"));
	}
}
