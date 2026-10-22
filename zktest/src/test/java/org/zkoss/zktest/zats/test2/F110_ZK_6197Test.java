/* F110_ZK_6197Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Oct 14 15:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6197Test extends WebDriverTestCase {

	private void resize(int width) {
		Dimension cur = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(width, cur.height));
		waitResponse();
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		resize(1500);
		assertTrue(jq("$gridXxl").hasClass("z-grid--stacking"), "xxl-none must be dropped as an invalid token");
		assertTrue(jq("$lbXxl").hasClass("z-listbox--stacking"), "xxl-none must be dropped as an invalid token");
		assertFalse(jq("$gridXl").hasClass("z-grid--stacking"), "a container past 1400px resolves to xl");
		assertFalse(jq("$lbXl").hasClass("z-listbox--stacking"), "a container past 1400px resolves to xl");

		resize(1100);
		assertTrue(jq("$gridXl").hasClass("z-grid--stacking"), "below 1200px the lg tier cascades from sm-1");
		assertTrue(jq("$lbXl").hasClass("z-listbox--stacking"), "below 1200px the lg tier cascades from sm-1");
	}
}
