/* B65_ZK_1679Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 17:23:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B65_ZK_1679Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 2;
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqWindow = jq("@window");
		Actions actions = getActions();
		actions.moveToElement(driver.findElement(jqWindow))
				.moveByOffset(jqWindow.outerWidth() / 2 - DRAG_THRESHOLD, jqWindow.outerHeight() / 2 - DRAG_THRESHOLD)
				.clickAndHold()
				.moveByOffset(50, 50)
				.release()
				.perform();
		waitResponse();
		assertEquals(50, jq(".z-hlayout").height(), 3, "you should not see green background bar expand to window size");
		assertEquals(100, jq(".z-hlayout").width(), 3, "you should not see green background bar expand to window size");
	}
}
