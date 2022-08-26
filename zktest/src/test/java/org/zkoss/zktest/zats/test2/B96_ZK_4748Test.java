/* B96_ZK_4748Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 01 09:37:14 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

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
		Assertions.assertEquals("focus on 2nd textbox", getZKLog());
		Assertions.assertTrue(jq(".z-bandbox-open").exists());

		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-bandbox-open").exists());

		click(jq(".z-bandbox-button:eq(1)"));
		waitResponse();
		click(jq("@listitem:eq(0)"));
		waitResponse();
		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-bandbox-open").exists());

		click(jq(".z-bandbox-button:eq(2)"));
		waitResponse(true);
		Assertions.assertTrue(jq("@treecell:contains(level 1)").isVisible());
		Assertions.assertTrue(jq(".z-tree-close").exists());
		click(jq(".z-tree-icon"));
		waitResponse();
		Assertions.assertTrue(jq(".z-tree-open").exists());
		Assertions.assertTrue(jq("@treecell:contains(level 2)").isVisible());
		Assertions.assertTrue(jq(".z-bandbox-open").exists());

		act.sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-bandbox-open").exists());
	}
}
