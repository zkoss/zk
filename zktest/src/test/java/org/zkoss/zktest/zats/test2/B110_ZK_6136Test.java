/* B110_ZK_6136Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jul 23 17:36:04 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6136Test extends WebDriverTestCase {
	private static final int NARROW = 600; // by default, sm range (576..767) -> stacking

	@Test
	public void testHiddenColumnStaysHiddenInStacking() {
		connect();
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$grid1").hasClass("z-grid--stacking"));
		assertTrue(jq(".z-grid-body tbody > tr.z-row:eq(0) td:eq(1)").hasClass("z-cell-hide-stacking"));
		assertFalse(jq(".z-grid-body tbody > tr.z-row:eq(0) td:eq(0)").hasClass("z-cell-hide-stacking"));
		assertFalse(jq(".z-grid-body tbody > tr.z-row:eq(0) td:eq(2)").hasClass("z-cell-hide-stacking"));
	}
}
