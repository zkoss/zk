/* B96_ZK_4915Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 18 10:11:11 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4915Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		for (int i = 0; i < 2; i++) {
			checkBadgeAligned(i);
		}
	}

	// IceBlue-only: a collapsed navbar centres the badge on the icon; Marble overlaps the icon's corner.
	@Test
	@Tag("IceBlueOnly")
	public void collapsedBadgeCenteredOnIcon() {
		connect();

		for (int i = 2; i < 4; i++) {
			checkBadgeAligned(i);
		}
	}

	// A collapsed navbar overlaps the badge on its icon's top-right corner, also when the navbar has a width (300px).
	@Test
	public void collapsedBadgeAtIconCorner() {
		connect();

		for (int i = 2; i < 4; i++) {
			// badge top - row top, and badge right - right edge of the icon column (icon + the row's right padding)
			String[] gaps = getEval("(function () {"
					+ " var row = jq('.z-nav-content')[" + i + "],"
					+ " r = row.getBoundingClientRect(),"
					+ " c = jq(row).children('i')[0].getBoundingClientRect(),"
					+ " b = jq('.z-nav-info')[" + i + "].getBoundingClientRect();"
					+ " return (b.top - r.top) + ',' + (b.right - c.right - parseFloat(getComputedStyle(row).paddingRight)); })()")
					.split(",");
			Assertions.assertEquals(4, Double.parseDouble(gaps[0]), 1, "badge " + i + " should sit 4px below the row's top");
			Assertions.assertTrue(Double.parseDouble(gaps[1]) <= 0.5, "badge " + i + " should stay inside the icon column, but its right edge is " + gaps[1] + "px past it");
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
