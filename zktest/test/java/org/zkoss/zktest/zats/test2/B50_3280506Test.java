/* B50_3280506Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 10:47:46 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3280506Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-bandbox-button"));
		waitResponse();
		sendKeys(jq(".z-textbox"), Keys.ENTER);
		waitResponse();

		Assert.assertTrue(jq(".z-bandbox-popup").is(":visible"));
		Assert.assertEquals("Fire on OK", jq(".z-label").text());
	}
}
