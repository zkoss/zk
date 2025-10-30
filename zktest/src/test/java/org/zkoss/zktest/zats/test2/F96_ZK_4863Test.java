/* F96_ZK_4863Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Jul 06 09:55:56 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F96_ZK_4863Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// empty test
		giuTextboxTest("", true);
		// CASE_INSENSITIVE test
		giuTextboxTest("aaa", false);
		// invalid input test
		giuTextboxTest("a", true);
		// valid input test
		giuTextboxTest("AAA", false);
		
		// default(g) test
		JQuery tbg = jq("@textbox").eq(8);
		click(tbg);
		waitResponse();
		type(tbg, "aaa");
		waitResponse();
		Assertions.assertTrue(hasError());
		click(tbg);
		waitResponse();
		type(tbg, "AAA");
		waitResponse();
		Assertions.assertFalse(hasError());
		
		// invalid flag(abc) test
		click(jq("@button:contains(invalid flags)"));
		waitResponse();
		Assertions.assertTrue(hasError());
		click(jq(".z-icon-times")); // close error
		waitResponse();
		// unsupported flag(y) test
		click(jq("@button:contains(unsupported flags)"));
		waitResponse();
		Assertions.assertTrue(hasError());
	}
	
	private void giuTextboxTest(String input, boolean errorExpected) {
		for (int i = 0; i < 8; i++) {
			JQuery textbox = jq("@textbox").eq(i);
			click(textbox);
			waitResponse();
			type(textbox, input);
			waitResponse();
		}
		if (errorExpected) { // shall see 8 errorbox
			Assertions.assertEquals(8, jq("@errorbox").length());
		} else { // check server validation if client side pass
			click(jq("@button:contains(server test)"));
			waitResponse();
			Assertions.assertFalse(hasError());
		}
	}
}
