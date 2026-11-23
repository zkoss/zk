/* F96_ZK_4863Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Jul 06 09:55:56 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertTrue(hasError());
		click(tbg);
		waitResponse();
		type(tbg, "AAA");
		waitResponse();
		Assert.assertFalse(hasError());
		
		// invalid flag(abc) test
		click(jq("@button:contains(invalid flags)"));
		waitResponse();
		Assert.assertTrue(hasError());
		click(jq(".z-icon-times")); // close error
		waitResponse();
		// unsupported flag(y) test
		click(jq("@button:contains(unsupported flags)"));
		waitResponse();
		Assert.assertTrue(hasError());
	}
	
	private void giuTextboxTest(String input, boolean errorExpected) {
		for (int i = 0; i < 8; i++) {
			JQuery textbox = jq("@textbox").eq(i);
			click(textbox);
			waitResponse();
			type(textbox, input);
			waitResponse();
		}
		if (errorExpected == true) { // shall see 8 errorbox
			Assert.assertEquals(8, jq("@errorbox").length());
		} else { // check server validation if client side pass
			click(jq("@button:contains(server test)"));
			waitResponse();
			Assert.assertFalse(hasError());
		}
	}
}
