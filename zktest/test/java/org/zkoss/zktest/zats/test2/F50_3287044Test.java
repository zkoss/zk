/* F50_3287044Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 12:36:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3287044Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals("2,000.02", jq("@doublebox:eq(0)").val());
		Assert.assertEquals("2,000.02", jq("@decimalbox:eq(0)").val());
		Assert.assertEquals("2,000.02", jq("@doublespinner:eq(0) input").val());

		Assert.assertEquals("2\u00A0000,02", jq("@doublebox:eq(1)").val());
		Assert.assertEquals("2\u00A0000,02", jq("@decimalbox:eq(1)").val());
		Assert.assertEquals("2\u00A0000,02", jq("@doublespinner:eq(1) input").val());

		Assert.assertEquals("2.000,02", jq("@doublebox:eq(2)").val());
		Assert.assertEquals("2.000,02", jq("@decimalbox:eq(2)").val());
		Assert.assertEquals("2.000,02", jq("@doublespinner:eq(2) input").val());

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("2,000.02", jq("@doublebox:eq(0)").val());
		Assert.assertEquals("2,000.02", jq("@decimalbox:eq(0)").val());
		Assert.assertEquals("2,000.02", jq("@doublespinner:eq(0) input").val());
		Assert.assertEquals("2,000.02", jq("@doublebox:eq(1)").val());
		Assert.assertEquals("2,000.02", jq("@decimalbox:eq(1)").val());
		Assert.assertEquals("2,000.02", jq("@doublespinner:eq(1) input").val());
		Assert.assertEquals("2,000.02", jq("@doublebox:eq(2)").val());
		Assert.assertEquals("2,000.02", jq("@decimalbox:eq(2)").val());
		Assert.assertEquals("2,000.02", jq("@doublespinner:eq(2) input").val());
	}
}
