/* B90_ZK_4452Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Mar 3 14:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B90_ZK_4452Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0, len = themesLinks.length(); i < len; i++) {
			click(themesLinks.eq(i));
			waitResponse();
			testErrorBtnStyle();
		}
	}
	
	private void testErrorBtnStyle() {
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals(jq(".z-bandbox-invalid").css("border"), jq(".z-bandbox-button").css("border-left"));
		Assertions.assertEquals(jq(".z-combobox-invalid").css("border"), jq(".z-combobox-button").css("border-left"));
		Assertions.assertEquals(jq(".z-datebox-invalid").css("border"), jq(".z-datebox-button").css("border-left"));
	}
}
