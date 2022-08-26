/* B70_ZK_2949Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 18:11:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2949Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 3;

	@Test
	public void testMVVM() {
		connect();
		waitResponse(true);
		Assertions.assertTrue(jq("@grid:eq(0) @column:last").attr("style").contains("display: none"));
		Assertions.assertEquals(0, jq("@grid:eq(1) @column:last").width(), 1);
		Assertions.assertEquals(0, jq("@grid:eq(2) @column:last").width(), 1);

		Actions actions = new Actions(driver);
		resizeColumn(actions, jq("@grid:eq(0) @column:eq(0)"), 100);
		resizeColumn(actions, jq("@grid:eq(1) @column:eq(0)"), 100);
		resizeColumn(actions, jq("@grid:eq(2) @column:eq(0)"), 100);
		waitResponse();

		click(jq("@button:contains(toggle flag (mvvm))"));
		waitResponse();

		Assertions.assertTrue(jq("@grid:eq(0) @column:last").width() > 3);
		Assertions.assertTrue(jq("@grid:eq(1) @column:last").width() > 3);
		Assertions.assertTrue(jq("@grid:eq(2) @column:last").width() > 3);

		click(jq("@button:contains(invalidate 1st grid)"));
		waitResponse();
		resizeColumn(actions, jq("@grid:eq(0) @column:eq(0)"), 100);
		waitResponse();
		click(jq("@button:contains(toggle flag (mvvm))"));
		waitResponse();
		Assertions.assertTrue(jq("@grid:eq(0) @column:contains(mvvm4)").width() > 3);
	}

	@Test
	public void testMVC() {
		connect();

		Assertions.assertEquals(false, jq("@grid:last @column:last").isVisible());

		Actions actions = new Actions(driver);
		resizeColumn(actions, jq("@grid:last @column:eq(0)"), 100);
		waitResponse();

		click(jq("@button:contains(toggle flag (mvc))"));
		waitResponse();

		Assertions.assertTrue(jq("@grid:last @column:last").width() > 3);
	}

	private void resizeColumn(Actions actions, JQuery col, int xOffset) {
		actions.moveToElement(driver.findElement(col))
				.moveByOffset(col.outerWidth() / 2 - DRAG_THRESHOLD, 0)
				.clickAndHold()
				.moveByOffset(xOffset, 0)
				.release()
				.perform();
	}
}
