/* B96_ZK_4915Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 18 10:11:11 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4915Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		for (int i = 0; i < 4; i++) {
			checkBadgeAligned(i);
		}
	}

	private void checkBadgeAligned(int index) {
		JQuery text = jq(".z-nav-text").eq(index);
		double textMiddle = text.offsetTop() + text.height() / 2;
		JQuery badge = jq(".z-nav-info").eq(index);
		double badgeMiddle = badge.offsetTop() + badge.height() / 2;
		JQuery icon = jq(".z-nav-content > i").eq(index);
		double iconMiddle = icon.offsetTop() + icon.height() / 2;

		if (index < 2) // collapsed=false
			Assertions.assertEquals(textMiddle, badgeMiddle, 2, "text and badgeText should be vertical aligned");
		Assertions.assertEquals(iconMiddle, badgeMiddle, 2, "icon and badgeText should be vertical aligned");
	}
}
