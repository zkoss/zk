/* B80_ZK_2937Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 16:46:33 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_2937Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery input = jq(".z-combobox-input");
		click(input);
		waitResponse();
		sendKeys(input, "ar");
		waitResponse();
		click(jq(".z-comboitem"));
		waitResponse();
		sendKeys(input, Keys.END);
		waitResponse();
		for (int i = 0; i < 2; i++) {
			sendKeys(input, Keys.BACK_SPACE);
			waitResponse();
		}
		Assertions.assertEquals("ar_", input.val());
	}
}
