/* B104_ZK_6072Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 13 17:48:08 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_6072Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		Actions act = getActions();
		act.moveToElement(toElement(jq(".z-panel").eq(1)), 0, -jq(".z-panel").eq(1).height() / 2 + 5)
			.clickAndHold()
			.moveToElement(toElement(jq(".kanban-col-processing")))
			.release()
			.perform();
		waitResponse();

		assertFalse(hasError());
		assertEquals(1, jq(".z-portalchildren").eq(0).toWidget().nChildren());
		assertEquals(1, jq(".z-portalchildren").eq(1).toWidget().nChildren());
		assertEquals(0, jq(".z-portalchildren").eq(2).toWidget().nChildren());
	}
}
