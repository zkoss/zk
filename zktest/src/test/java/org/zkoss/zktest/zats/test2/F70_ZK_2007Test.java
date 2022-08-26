/* F70_ZK_2007Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 14 12:16:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_2007Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery item = jq(".z-treerow:contains(Item)");
		JQuery item1 = jq(".z-treerow:contains(Item1)");
		JQuery item2 = jq(".z-treerow:contains(Item2)");
		Actions actions = getActions();

		actions.moveToElement(toElement(item))
				.contextClick()
				.perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-popup").exists(), "open the popup");
		actions.moveByOffset(-2, -2) // avoid clicking on the popup
				.contextClick()
				.perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-popup").isVisible(), "it will close");

		actions.moveToElement(toElement(item1))
				.click()
				.perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-menupopup").exists(),
				"open the context menu");
		actions.moveByOffset(-2, -2)
				.click()
				.perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-menupopup").isVisible(), "it will close");

		actions.moveToElement(toElement(item2))
				.contextClick()
				.perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-menupopup").exists(),
				"open the context menu");
		actions.moveByOffset(-2, -2)
				.contextClick()
				.perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-menupopup").isVisible(), "it will close");
	}
}
