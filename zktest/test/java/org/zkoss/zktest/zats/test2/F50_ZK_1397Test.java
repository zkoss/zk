/* F50_ZK_1397Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 16:48:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_ZK_1397Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertTrue(widget("@treerow:contains(item b):eq(0)").$n("open").exists());
		Assert.assertFalse(widget("@treerow:contains(item b):eq(1)").$n("open").exists());
	}
}
