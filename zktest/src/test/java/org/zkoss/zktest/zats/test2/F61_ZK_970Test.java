/* F61_ZK_970Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 12:48:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F61_ZK_970Test extends WebDriverTestCase {
	@Test
	public void testFirstTabbox() {
		connect();

		selectComboitem(widget("@combobox"), 0);
		Assertions.assertEquals("item 1", jq("@combobox input").val());

		click(jq("@tab:eq(1) .z-caption-label"));
		waitResponse(true);
		Assertions.assertTrue(jq("@tab:eq(1)").hasClass("z-tab-selected"));

		click(widget("@tab:eq(1)").$n("cls"));
		waitResponse();
		Assertions.assertEquals(2, jq("@tabbox:first @tab").length());
	}

	@Test
	public void testSecondTabbox() {
		connect();

		click(jq("@tabbox:eq(1) @tab:eq(1) .z-caption-label"));
		waitResponse(true);
		Assertions.assertTrue(jq("@tabbox:eq(1) @tab:eq(1)").hasClass("z-tab-selected"));

		for (int i = 0; i < 4; i++) {
			click(widget("@tabbox:eq(1)").$n("right"));
			waitResponse();
		}
		selectComboitem(widget("@tabbox:eq(1) @combobox"), 0);
		Assertions.assertEquals("item 1", jq("@tabbox:eq(1) @combobox input").val());

		click(widget("@tabbox:eq(1)").$n("right"));
		waitResponse();
		click(widget("@tabbox:eq(1) @tab:eq(3)").$n("cls"));
		waitResponse();
		Assertions.assertEquals(4, jq("@tabbox:eq(1) @tab").length());
	}

	@Test
	public void testThirdTabbox() {
		connect();

		click(jq("@tabbox:last @tab:eq(1) .z-caption-label"));
		waitResponse(true);
		Assertions.assertTrue(jq("@tabbox:last @tab:eq(1)").hasClass("z-tab-selected"));

		click(widget("@tabbox:last @tab:eq(1)").$n("cls"));
		waitResponse();
		Assertions.assertEquals(5, jq("@tabbox:last @tab").length());

		click(jq("@checkbox"));
		waitResponse();

		click(jq("@tabbox:last @tab:eq(0) .z-caption-label"));
		waitResponse(true);
		Assertions.assertTrue(jq("@tabbox:last @tab:eq(0)").hasClass("z-tab-selected"));

		click(widget("@tabbox:last @tab:eq(0)").$n("cls"));
		waitResponse();
		Assertions.assertEquals(4, jq("@tabbox:last @tab").length());
	}
}
