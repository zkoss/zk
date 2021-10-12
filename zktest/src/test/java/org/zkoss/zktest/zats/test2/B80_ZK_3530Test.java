/* B80_ZK_3530Test.java

		Purpose:
                
		Description:
                
		History:
				Wed Mar 27 09:58:33 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B80_ZK_3530Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		sendKeys(jq(".z-textbox"), Keys.ESCAPE);
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
