/* B70_ZK_2769Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 04 15:48:20 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2769Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 3;

	@Test
	public void test() {
		connect();

		JQuery listheader = jq(".z-listheader").eq(0);
		int widthBefore = listheader.outerWidth();
		resizeColumn(getActions(), listheader);
		waitResponse();

		int widthAfter = listheader.outerWidth();
		Assertions.assertNotEquals(widthAfter, widthBefore);
		Assertions.assertEquals(widthAfter, jq(".z-listcell").eq(0).outerWidth(), 1);
		Assertions.assertEquals(widthAfter, jq(".z-listfooter").eq(0).outerWidth(), 1);
	}

	private void resizeColumn(Actions actions, JQuery col) {
		int colWidth = col.outerWidth();
		actions.moveToElement(driver.findElement(col))
				.moveByOffset(colWidth / 2 - DRAG_THRESHOLD, 0)
				.clickAndHold()
				.moveByOffset(-50, 0)
				.release()
				.perform();
		int colWidthAfter = col.outerWidth();
		assertThat("resize failed", colWidthAfter, lessThan(colWidth));
	}
}
