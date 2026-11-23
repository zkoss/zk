/* B90_ZK_4409Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Mar 02 17:30:49 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4409Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0; i < themesLinks.length(); i++) {
			click(themesLinks.eq(i));
			waitResponse();
			checkCombobuttonGap();
			checkButtonsAlign();
		}
	}

	private void checkCombobuttonGap() {
		JQuery jqCombobutton = jq("@combobutton").eq(0);
		JQuery jqDiv = jq("$div1");
		int combobuttonOffsetBottom = jqCombobutton.offsetTop() - jqCombobutton.outerHeight();
		int divOffsetBottom = jqDiv.offsetTop() - jqDiv.outerHeight();
		Assert.assertEquals(combobuttonOffsetBottom, combobuttonOffsetBottom);
	}

	private void checkButtonsAlign() {
		JQuery jqCombobutton = jq("@combobutton").eq(1);
		JQuery jqButton = jq("@button").eq(0);
		int combobuttonHorizontalCenterline = jqCombobutton.offsetTop() + jqCombobutton.outerHeight() / 2;
		int buttonHorizontalCenterline = jqButton.offsetTop() + jqButton.outerHeight() / 2;
		Assert.assertEquals(combobuttonHorizontalCenterline, buttonHorizontalCenterline, 1);
	}
}
