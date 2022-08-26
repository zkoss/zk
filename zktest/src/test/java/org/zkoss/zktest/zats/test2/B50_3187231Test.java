/* B50_3187231Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 09:54:10 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3187231Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery include = jq(".z-include");
		Assertions.assertTrue(include.exists());
		Assertions.assertTrue(include.find(".z-window").exists());
	}
}
