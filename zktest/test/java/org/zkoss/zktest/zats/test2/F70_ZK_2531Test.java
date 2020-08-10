/* F70_ZK_2531Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 17:31:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_2531Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assert.assertEquals("1", jq("@checkbox input").attr("tabindex"));
		Assert.assertEquals("2", jq("@tree .z-focus-a").attr("tabindex"));
		Assert.assertEquals("3", jq("@button").attr("tabindex"));
		Assert.assertEquals("4", jq("@listbox .z-focus-a").attr("tabindex"));
	}
}
