/* B80_ZK_3082Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 12:59:38 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B80_ZK_3082Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		jq(".z-chosenbox-input").forEach(input -> {
			sendKeys(input, "i");
			waitResponse();
			sendKeys(input, Keys.BACK_SPACE);
			waitResponse();
			Assert.assertEquals("onSearching\nonSearching", getZKLog());
			closeZKLog();
		});
	}
}
