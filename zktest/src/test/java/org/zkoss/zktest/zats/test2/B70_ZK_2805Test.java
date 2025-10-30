/* B70_ZK_2805Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 12:38:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;

/**
 * @author rudyhuang
 */
public class B70_ZK_2805Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Element inp = widget("@chosenbox").$n("inp");
		click(inp);
		sendKeys(inp, "bbb" + Keys.ENTER);
		waitResponse();

		assertNoJSError();
	}
}
