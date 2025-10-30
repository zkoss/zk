/* F96_ZK_4874Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 04 14:16:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F96_ZK_4874Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		Assertions.assertEquals("badge", jq(".z-navitem-info:eq(0)").text().trim());
		Assertions.assertEquals("badge", jq(".z-navitem-info:eq(1)").text().trim());
		Assertions.assertEquals("badge", jq(".z-navitem-info:eq(2)").text().trim());
		Assertions.assertEquals("badge", jq(".z-navitem-info:eq(3)").text().trim());
		
		click(jq("@button:contains(change)"));
		waitResponse();
		Assertions.assertEquals("changed", jq(".z-navitem-info:eq(0)").text().trim());
		Assertions.assertEquals("changed", jq(".z-navitem-info:eq(1)").text().trim());
		Assertions.assertEquals("changed", jq(".z-navitem-info:eq(2)").text().trim());
		Assertions.assertEquals("changed", jq(".z-navitem-info:eq(3)").text().trim());
		
		click(jq("@button:contains(clear)"));
		waitResponse();
		Assertions.assertFalse(jq(".z-navitem-info:eq(0)").exists());
		Assertions.assertFalse(jq(".z-navitem-info:eq(1)").exists());
		Assertions.assertFalse(jq(".z-navitem-info:eq(2)").exists());
		Assertions.assertFalse(jq(".z-navitem-info:eq(3)").exists());
		
		click(jq("@button:contains(br)"));
		waitResponse();
		Assertions.assertEquals("<br/>", jq(".z-navitem-info:eq(0)").text().trim());
		Assertions.assertEquals("<br/>", jq(".z-navitem-info:eq(1)").text().trim());
		Assertions.assertEquals("<br/>", jq(".z-navitem-info:eq(2)").text().trim());
		Assertions.assertEquals("<br/>", jq(".z-navitem-info:eq(3)").text().trim());
	}
}
