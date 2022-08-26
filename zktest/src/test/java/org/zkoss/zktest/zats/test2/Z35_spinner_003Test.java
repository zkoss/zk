/* Z35_spinner_003Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 15:49:50 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Z35_spinner_003Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		type(jq(".z-spinner-input"), "11");
		waitResponse();
		Assertions.assertFalse(hasError());
		type(jq(".z-spinner-input"), "-11");
		waitResponse();
		Assertions.assertFalse(hasError());
		click(jq("@button"));
		waitResponse();
		for (int i = 0; i < 6; i++) {
			click(jq(".z-spinner-down"));
			waitResponse();
			if (i >= 4) {
				Assertions.assertEquals("-9", jq(".z-spinner-input").val());
			}
		}
		click(jq("@button"));
		waitResponse();
		for (int i = 0; i < 5; i++) {
			click(jq(".z-spinner-up"));
			waitResponse();
			if (i >= 3) {
				Assertions.assertEquals("9", jq(".z-spinner-input").val());
			}
		}
	}
}
