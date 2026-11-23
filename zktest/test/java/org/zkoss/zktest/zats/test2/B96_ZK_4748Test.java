/* B96_ZK_4748Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 01 09:37:14 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4748Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());

		click(jq(".z-bandbox-button:eq(0)"));
		waitResponse();
		click(jq("@textbox:eq(0)"));
		waitResponse();
		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertEquals("focus on 2nd textbox", getZKLog());
		Assert.assertTrue(jq(".z-bandbox-open").exists());

		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertFalse(jq(".z-bandbox-open").exists());

		click(jq(".z-bandbox-button:eq(1)"));
		waitResponse();
		click(jq("@listitem:eq(0)"));
		waitResponse();
		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertFalse(jq(".z-bandbox-open").exists());

		click(jq(".z-bandbox-button:eq(2)"));
		waitResponse(true);
		Assert.assertTrue(jq("@treecell:contains(level 1)").isVisible());
		Assert.assertTrue(jq(".z-tree-close").exists());
		click(jq(".z-tree-icon"));
		waitResponse();
		Assert.assertTrue(jq(".z-tree-open").exists());
		Assert.assertTrue(jq("@treecell:contains(level 2)").isVisible());
		Assert.assertTrue(jq(".z-bandbox-open").exists());

		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assert.assertFalse(jq(".z-bandbox-open").exists());
	}
}
