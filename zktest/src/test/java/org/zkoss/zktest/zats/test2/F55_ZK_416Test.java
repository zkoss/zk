/* F55_ZK_416Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 16:56:12 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F55_ZK_416Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = getActions();
		actions.dragAndDrop(
					toElement(jq("@listitem:contains(ZK Forge)")),
					toElement(jq("@listitem:contains(ZK Studio)")))
				.perform();
		waitResponse();
		Assert.assertFalse(jq("$left @listitem:contains(ZK Forge)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK Forge)").exists());

		click(jq("@listitem:contains(ZK Mobile)"));
		waitResponse();
		actions.dragAndDrop(
					toElement(jq("@listitem:contains(ZK Mobile)")),
					toElement(jq("@listitem:contains(ZK Studio)")))
				.perform();
		waitResponse();
		Assert.assertFalse(jq("$left @listitem:contains(ZK Mobile)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK Mobile)").exists());

		click(jq("@listitem:contains(ZK GWT)"));
		waitResponse();
		actions.dragAndDrop(
					toElement(jq("@listitem:contains(ZK JSF)")),
					toElement(jq("@listitem:contains(ZK Studio)")))
				.perform();
		waitResponse();
		Assert.assertFalse(jq("$left @listitem:contains(ZK GWT)").exists());
		Assert.assertFalse(jq("$left @listitem:contains(ZK JSF)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK GWT)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK JSF)").exists());
	}

	@Test
	public void testMultiple() {
		connect();

		click(jq("@listitem:contains(ZK Forge)"));
		waitResponse();

		Actions actions = getActions();
		actions.keyDown(Keys.CONTROL)
				.click(toElement(jq("@listitem:contains(ZK GWT)")))
				.keyUp(Keys.CONTROL)
				.dragAndDrop(
						toElement(jq("@listitem:contains(ZK GWT)")),
						toElement(jq("@listitem:contains(ZK Studio)")))
				.perform();
		waitResponse();
		Assert.assertFalse(jq("$left @listitem:contains(ZK Forge)").exists());
		Assert.assertFalse(jq("$left @listitem:contains(ZK GWT)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK Forge)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK GWT)").exists());

		click(jq("@listitem:contains(ZK Mobile)"));
		waitResponse();
		actions.keyDown(Keys.SHIFT)
				.click(toElement(jq("@listitem:contains(ZK JSP)")))
				.keyUp(Keys.SHIFT)
				.dragAndDrop(
						toElement(jq("@listitem:contains(ZK Spring)")),
						toElement(jq("@listitem:contains(ZK Studio)")))
				.perform();
		waitResponse();
		Assert.assertFalse(jq("$left @listitem:contains(ZK Mobile)").exists());
		Assert.assertFalse(jq("$left @listitem:contains(ZK JSF)").exists());
		Assert.assertFalse(jq("$left @listitem:contains(ZK JSP)").exists());
		Assert.assertFalse(jq("$left @listitem:contains(ZK Spring)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK Mobile)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK JSF)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK JSP)").exists());
		Assert.assertTrue(jq("$right @listitem:contains(ZK Spring)").exists());
	}
}
