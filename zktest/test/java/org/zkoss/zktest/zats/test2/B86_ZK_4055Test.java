/* B86_ZK_4055Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 13 10:57:34 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4055Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertTrue(jq("#div1").exists());
		Assert.assertTrue(jq("#div2").exists());
		Assert.assertFalse(jq("#div3").exists());
		Assert.assertTrue(jq("#div4").exists());
		Assert.assertFalse(jq("#div5").exists());

		Assert.assertTrue("Missing ZK label!", jq("#div1 > span").exists());
		Assert.assertTrue("Missing ZK label!", jq("#div2 > span").exists());
		Assert.assertTrue("Missing ZK label!", jq("#div4 > span").exists());
	}
}
