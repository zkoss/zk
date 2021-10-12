/* B70_ZK_2941Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 16:23:14 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2941Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sendKeys(jq("body"), Keys.TAB, Keys.ENTER);
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").isVisible());
		Assert.assertEquals("click event - combobutton", jq(".z-messagebox").text().trim());

		click(jq(".z-messagebox-button"));
		waitResponse();

		sendKeys(jq("@combobutton"), Keys.SPACE);
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").isVisible());
		Assert.assertEquals("click event - combobutton", jq(".z-messagebox").text().trim());

		click(jq(".z-messagebox-button"));
		waitResponse();

		sendKeys(jq("@combobutton"), Keys.DOWN);
		waitResponse();
		Assert.assertTrue(jq("@menupopup").isVisible());
		sendKeys(jq("@combobutton"), Keys.ESCAPE);
		waitResponse();
		Assert.assertFalse(jq("@menupopup").isVisible());
	}
}
