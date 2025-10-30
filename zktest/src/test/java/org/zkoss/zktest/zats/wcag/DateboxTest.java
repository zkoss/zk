/* DateboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 10:57:36 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class DateboxTest extends WcagTestCase {
	@Test
	public void testDate() {
		connect();

		JQuery db = jq("@datebox:eq(0)");
		click(db);
		waitResponse();

		Actions actions = getActions();
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT)
				.pause(200)
				.sendKeys(Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP)
				.perform();
		waitResponse();
		verifyA11y();

		actions.sendKeys(Keys.SPACE).perform();
		waitResponse();
		verifyA11y();
	}

	@Test
	public void testDateTime() {
		connect();

		JQuery db = jq("@datebox:eq(1)");
		click(db);
		waitResponse();

		Actions actions = getActions();
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT)
				.pause(200)
				.sendKeys(Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP)
				.perform();
		waitResponse();
		verifyA11y();

		actions.sendKeys(Keys.SPACE).perform();
		waitResponse();
		verifyA11y();
	}

	@Test
	public void testFocus() {
		connect();

		click(jq("@datebox:eq(4)"));
		waitResponse();
		Actions actions = getActions();
		actions.keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT)
				.pause(200)
				.sendKeys(Keys.TAB, Keys.TAB, Keys.TAB)
				.perform();
		waitResponse();
		Assertions.assertTrue(jq(widget("@datebox:eq(4)").$n("pp")).isVisible());
	}
}
