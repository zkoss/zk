/* DateboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 10:57:36 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class DateboxTest extends WcagTestCase {
	@Test
	@Ignore("Needs aXe >= 3.5.6, https://github.com/dequelabs/axe-core/pull/2304")
	public void testDate() {
		connect();

		JQuery db = jq("@datebox:eq(0)");
		click(db);
		waitResponse();

		Actions actions = getActions();
		actions.sendKeys(Keys.chord(Keys.ALT, Keys.ARROW_DOWN))
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
	@Ignore("Needs aXe >= 3.5.6, https://github.com/dequelabs/axe-core/pull/2304")
	public void testDateTime() {
		connect();

		JQuery db = jq("@datebox:eq(1)");
		click(db);
		waitResponse();

		Actions actions = getActions();
		actions.sendKeys(Keys.chord(Keys.ALT, Keys.ARROW_DOWN))
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
		actions.sendKeys(Keys.chord(Keys.ALT, Keys.ARROW_DOWN))
				.pause(200)
				.sendKeys(Keys.TAB, Keys.TAB, Keys.TAB)
				.perform();
		waitResponse();
		Assert.assertTrue(jq(widget("@datebox:eq(4)").$n("pp")).isVisible());
	}
}
