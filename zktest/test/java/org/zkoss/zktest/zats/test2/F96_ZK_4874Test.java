/* F96_ZK_4874Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 04 14:16:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F96_ZK_4874Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		Assert.assertEquals("badge", jq(".z-navitem-info:eq(0)").text().trim());
		Assert.assertEquals("badge", jq(".z-navitem-info:eq(1)").text().trim());
		Assert.assertEquals("badge", jq(".z-navitem-info:eq(2)").text().trim());
		Assert.assertEquals("badge", jq(".z-navitem-info:eq(3)").text().trim());
		
		click(jq("@button:contains(change)"));
		waitResponse();
		Assert.assertEquals("changed", jq(".z-navitem-info:eq(0)").text().trim());
		Assert.assertEquals("changed", jq(".z-navitem-info:eq(1)").text().trim());
		Assert.assertEquals("changed", jq(".z-navitem-info:eq(2)").text().trim());
		Assert.assertEquals("changed", jq(".z-navitem-info:eq(3)").text().trim());
		
		click(jq("@button:contains(clear)"));
		waitResponse();
		Assert.assertFalse(jq(".z-navitem-info:eq(0)").exists());
		Assert.assertFalse(jq(".z-navitem-info:eq(1)").exists());
		Assert.assertFalse(jq(".z-navitem-info:eq(2)").exists());
		Assert.assertFalse(jq(".z-navitem-info:eq(3)").exists());
		
		click(jq("@button:contains(br)"));
		waitResponse();
		Assert.assertEquals("<br/>", jq(".z-navitem-info:eq(0)").text().trim());
		Assert.assertEquals("<br/>", jq(".z-navitem-info:eq(1)").text().trim());
		Assert.assertEquals("<br/>", jq(".z-navitem-info:eq(2)").text().trim());
		Assert.assertEquals("<br/>", jq(".z-navitem-info:eq(3)").text().trim());
	}
}
