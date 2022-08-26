/* B30_1852304Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 12 12:26:08 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B30_1852304Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		getActions().moveToElement(toElement(jq(".z-menu"))).perform();
		waitResponse(true);

		click(jq(".z-menuitem"));
		waitResponse();
		Assertions.assertTrue(jq(".z-popup .z-listbox").exists());
	}
}
