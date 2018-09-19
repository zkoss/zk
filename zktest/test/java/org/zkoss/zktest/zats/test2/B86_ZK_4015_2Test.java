/* B86_ZK_4015_2Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 10 14:49:02 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4015_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		testBandbox(jq("$bb1"), 300); // click the right blank area

		((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollWidth, document.body.scrollHeight)");
		testBandbox(jq("$bb2"), -300); // click the left blank area
	}

	private void testBandbox(JQuery bandbox, int xOffsetFromBtn) {
		JQuery btn = bandbox.find(".z-bandbox-button");
		click(btn);
		waitResponse();
		Assert.assertTrue(bandbox.find("@bandpopup").isVisible());

		Actions actions = new Actions(driver);
		actions.moveToElement(toElement(btn), xOffsetFromBtn, 0).click().perform();
		waitResponse();
		Assert.assertFalse("The popup didn't hide", bandbox.find("@bandpopup").isVisible());
	}
}
