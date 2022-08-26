/* B50_3323593Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 02 15:55:45 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3323593Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-timebox-up"));
		waitResponse();
		Assertions.assertFalse(jq(".z-timebox-input").val().contains("z"));
		
		click(jq(".z-timebox-down"));
		waitResponse();
		Assertions.assertFalse(jq(".z-timebox-input").val().contains("z"));
	}
}
