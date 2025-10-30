/* B80_ZK_3017Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:49:01 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3017Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery label = jq(".z-label");
		for (int i = 0; i < 2; i++) {
			JQuery textbox = jq(".z-textbox").eq(i);
			sendKeys(textbox, "123456");
			waitResponse();
			click(label);
			waitResponse();
			JQuery errorbox = jq(".z-errorbox");
			Assertions.assertTrue(errorbox.exists());
			Assertions.assertEquals("myMsg", errorbox.find(".z-errorbox-content").text());
			for (int j = 0; j < 6; j++) {
				sendKeys(textbox, Keys.BACK_SPACE);
				waitResponse();
			}
			sendKeys(textbox, "123,45");
			waitResponse();
			click(label);
			waitResponse();
			Assertions.assertFalse(errorbox.exists());
		}
	}
}
