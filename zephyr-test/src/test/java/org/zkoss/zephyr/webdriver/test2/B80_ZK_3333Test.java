/* B80_ZK_3333Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 16:30:49 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3333Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		JQuery editor = jq(".z-tbeditor-editor");
		sendKeys(editor, Keys.END);
		waitResponse();
		sendKeys(editor, "ss");
		waitResponse();
		click(jq(".z-button"));
		waitResponse();
		Assertions.assertTrue(jq("$mid").text().contains("PROXY MSG : original messagess"));
		Assertions.assertEquals("original messagess", jq("$final").text());
	}
}
