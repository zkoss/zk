/* F50_3306718Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 18:03:02 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3306718Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = getActions();
		actions.dragAndDrop(
					toElement(jq("@treecell:contains(Item 2.1)")),
					toElement(jq("@treecell:contains(Item 1)")))
				.perform();
		waitResponse();
		Assertions.assertTrue(jq("@treerow:contains(Item 2.1):first").is("@treerow:first"));

		actions.dragAndDrop(
					toElement(jq("@treecell:contains(Item 3)")),
					toElement(jq("@treecell:contains(Item 2.1.2)")))
				.perform();
		waitResponse();
		Assertions.assertEquals(
				toElement(jq("@treerow:contains(Item 2.1.2)")),
				toElement(jq("@treerow:contains(Item 3)").next()));
	}
}
