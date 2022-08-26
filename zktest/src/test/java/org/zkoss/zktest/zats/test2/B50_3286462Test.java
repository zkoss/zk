/* B50_3286462Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 11:12:26 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3286462Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery buttons = jq(".z-button");

		click(buttons.eq(0));
		waitResponse();
		Assertions.assertTrue(jq(".z-messagebox-window").exists());
		click(jq(".z-messagebox-button"));
		waitResponse();

		click(buttons.eq(1));
		waitResponse();
		for (int i = 1; i <= 10; i++) {
			Assertions.assertTrue(jq("div:contains(" + i + ". I am a native component )").exists());
		}
	}
}
