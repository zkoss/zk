/* F50_3307322Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 11 10:41:18 CST 2019, Created by rudyhuang

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
public class F50_3307322Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox"));
		waitResponse();
		sendKeys(jq("@textbox"), "aaa", Keys.BACK_SPACE);
		waitResponse();

		Assert.assertEquals("8", getMessageBoxContent());
	}
}
