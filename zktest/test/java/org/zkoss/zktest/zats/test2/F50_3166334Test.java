/* F50_3166334Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 17:53:51 CST 2019, Created by rudyhuang

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
public class F50_3166334Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox"));
		waitResponse();
		sendKeys(jq("@textbox"), Keys.ENTER);
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").isVisible());
		Assert.assertEquals("inside onOK", getMessageBoxContent());
	}
}
