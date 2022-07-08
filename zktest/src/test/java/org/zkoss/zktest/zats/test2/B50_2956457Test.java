/* B50_2956457Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 15:22:24 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2956457Test extends WebDriverTestCase {
	@Test
	public void testCombobox() {
		connect();

		click(jq("@button:contains(Hello)"));
		waitResponse();

		Assert.assertEquals(0, driver.findElements(By.xpath("//*[text()=\"David1\"]")).size());
		Assert.assertEquals(0, driver.findElements(By.xpath("//*[text()=\"Mary2\"]")).size());
		Assert.assertEquals(0, driver.findElements(By.xpath("//*[text()=\"John3\"]")).size());
	}

	@Test
	public void testGroupbox() {
		connect();

		click(jq("@button:contains(prepend child)"));
		waitResponse();
		click(jq("@button:contains(append child)"));
		waitResponse();
		click(jq("@button:contains(toggle)"));
		waitResponse(true);

		Assert.assertEquals("child before", jq("@grid").prev().text());
		Assert.assertEquals("child after", jq("@grid").next().text());
	}

	@Test
	public void testTree() {
		connect();

		click(jq("@button:contains(test tree)"));
		waitResponse();
		click(widget("@treeitem").$n("open"));
		waitResponse();

		Assert.assertTrue(jq("@treeitem:contains(Microsoft)").isVisible());
		Assert.assertTrue(jq("@treeitem:contains(IBM)").isVisible());
	}

	@Test
	public void testBandbox() {
		connect();

		click(jq("@button:contains(test bandbox)"));
		waitResponse();
		click(widget("@bandbox").$n("btn"));
		waitResponse();

		Assert.assertTrue(jq(".z-bandpopup:contains(hi)").isVisible());
	}

	@Test
	public void testContextMenu() {
		connect();

		click(jq("@button:contains(test context menu)"));
		waitResponse();
		rightClick(jq("@label:contains(hello)"));
		waitResponse();

		Assert.assertTrue(jq("@menuitem:contains(More)").isVisible());
		Assert.assertFalse(jq("@menuitem:contains(Great)").isVisible());

		click(jq("@menu:contains(Sort)"));
		Assert.assertTrue(jq("@menuitem:contains(Great)").isVisible());
	}

	@Test
	public void testPopup() {
		connect();

		click(jq("@button:contains(test popup)"));
		waitResponse();
		click(jq("@label:contains(what happens)"));
		waitResponse();

		Assert.assertTrue(jq(".z-popup:contains(More and better)").isVisible());
	}
}
