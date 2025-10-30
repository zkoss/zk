/* B50_2925671Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 12:48:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;

/**
 * @author rudyhuang
 */
public class B50_2925671Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Element btn = widget("@combobox").$n("btn");
		click(btn);
		sleep(100);
		click(btn);
		waitResponse(true);

		Assertions.assertFalse(jq(".z-combobox-popup").isVisible(), "Should be close correctly");
	}
}
