/* B70_ZK_2941Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 16:23:14 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2941Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sendKeys(jq("body"), Keys.TAB, Keys.ENTER);
		waitResponse();
		Assertions.assertTrue(jq(".z-messagebox-window").isVisible());
		Assertions.assertEquals("click event - combobutton", jq(".z-messagebox").text().trim());

		click(jq(".z-messagebox-button"));
		waitResponse();

		sendKeys(jq("@combobutton"), Keys.SPACE);
		waitResponse();
		Assertions.assertTrue(jq(".z-messagebox-window").isVisible());
		Assertions.assertEquals("click event - combobutton", jq(".z-messagebox").text().trim());

		click(jq(".z-messagebox-button"));
		waitResponse();

		sendKeys(jq("@combobutton"), Keys.DOWN);
		waitResponse();
		Assertions.assertTrue(jq("@menupopup").isVisible());
		sendKeys(jq("@combobutton"), Keys.ESCAPE);
		waitResponse();
		Assertions.assertFalse(jq("@menupopup").isVisible());
	}
}
