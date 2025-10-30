/* B95_ZK_4550Test.java

		Purpose:
		
		Description:
		
		History:
				Mon May 18 17:06:19 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ElementNotInteractableException;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4550Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery orientButtons = jq("@button");
		
		for (int i = 0; i < 4; i++) { // test different orient
			click(orientButtons.eq(i));
			waitResponse();
			try {
				click(jq("$target"));
				waitResponse();
			} catch (ElementNotInteractableException e) {
				String errorMsg = "The tab2 inside nested tabbox should be visible.";
				Assertions.fail(errorMsg);
			}
		}
	}
}
