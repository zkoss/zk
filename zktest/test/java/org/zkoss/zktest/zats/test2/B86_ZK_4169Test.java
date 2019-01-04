/* B86_ZK_4169Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 03 12:49:11 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4169Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals("123 XYZA", jq("@intbox:eq(0)").val());
		Assert.assertEquals("123 XYZAB", jq("@intbox:eq(1)").val());
		Assert.assertEquals("123.0 XYZAB", jq("@intbox:eq(2)").val());
		Assert.assertEquals("123 XYZABC", jq("@intbox:eq(3)").val());
		Assert.assertEquals("123.0 XYZABC", jq("@intbox:eq(4)").val());
		Assert.assertEquals("123 XYZABCDEF", jq("@doublebox:eq(0)").val());
		Assert.assertEquals("123.0 XYZABCDEF", jq("@doublebox:eq(1)").val());
		Assert.assertEquals("123 XYZABCDEFGHIJKLM", jq("@doublebox:eq(2)").val());
		Assert.assertEquals("123.0 XYZABCDEFGHIJKLM", jq("@doublebox:eq(3)").val());
	}
}
