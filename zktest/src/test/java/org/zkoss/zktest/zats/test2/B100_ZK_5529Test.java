/* B100_ZK_5529Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 14 15:01:56 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B100_ZK_5529Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@treecell:contains(node-2-2)"));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertTrue(jq(".z-treerow-focus:contains(node-1-0)").exists());
	}
}
