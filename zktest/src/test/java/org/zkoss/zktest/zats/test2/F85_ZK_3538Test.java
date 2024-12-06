/* F85_ZK_3538Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 06 14:57:52 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.FirefoxWebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F85_ZK_3538Test extends FirefoxWebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery textbox = jq(".z-textbox");
		click(textbox);

		Actions actions = getActions();
		actions.sendKeys("ch").perform();
		waitResponse(true);
		assertTrue(textbox.is(":focus"));
		click(jq(".z-tree-icon:eq(1)"));
		waitResponse(true);
		assertEquals("tree is on focus", getZKLog());
	}
}
