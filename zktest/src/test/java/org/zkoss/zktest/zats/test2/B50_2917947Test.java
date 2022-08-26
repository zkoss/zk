/* B50_2917947Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 11:41:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2917947Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 3;

	@Test
	public void test() {
		connect();

		Actions actions = new Actions(driver);
		JQuery columns = jq("@column");
		resizeColumn(actions, columns.eq(3));
		resizeColumn(actions, columns.eq(2));
		resizeColumn(actions, columns.eq(1));
		resizeColumn(actions, columns.eq(0));
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
