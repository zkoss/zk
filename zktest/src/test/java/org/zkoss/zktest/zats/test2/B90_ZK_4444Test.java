/* B90_ZK_4444Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 05 10:32:42 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B90_ZK_4444Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0, len = themesLinks.length(); i < len; i++) {
			click(themesLinks.eq(i));
			waitResponse();
			checkToolbarbuttonGap();
			checkButtonsAlign();
		}
	}
	
	private void checkToolbarbuttonGap() {
		JQuery jqToolbarbutton = jq("@toolbarbutton").eq(0);
		JQuery jqDiv = jq("$div1");
		int toolbarbuttonOffsetBottom = jqToolbarbutton.offsetTop() - jqToolbarbutton.outerHeight();
		int divOffsetBottom = jqDiv.offsetTop() - jqDiv.outerHeight();
		Assertions.assertEquals(toolbarbuttonOffsetBottom, toolbarbuttonOffsetBottom);
	}
	
	private void checkButtonsAlign() {
		JQuery jqToolbarbutton = jq("@toolbarbutton").eq(1);
		JQuery jqButton = jq("@button").eq(0);
		int toolbarbuttonHorizontalCenterline = jqToolbarbutton.offsetTop() + jqToolbarbutton.outerHeight() / 2;
		int buttonHorizontalCenterline = jqButton.offsetTop() + jqButton.outerHeight() / 2;
		Assertions.assertEquals(toolbarbuttonHorizontalCenterline, buttonHorizontalCenterline, 1);
	}
}
