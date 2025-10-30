/* B90_ZK_4389Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb 06 17:47:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B90_ZK_4389Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		checkGroupbox(jq("@groupbox:eq(0)"));
		checkGroupbox(jq("@groupbox:eq(1)"));

		checkNestedGroupboxDiv();
		checkNestedDivGroupbox();
	}

	private void checkGroupbox(JQuery groupbox) {
		click(groupbox);
		waitResponse(true);

		click(groupbox.find("@button:visible"));
		waitResponse();
		Assertions.assertEquals(1, groupbox.find("@button:visible").length());
		Assertions.assertEquals("Button2", groupbox.find("@button:visible").text());
	}

	private void checkNestedGroupboxDiv() {
		click(jq("$gb1"));
		waitResponse(true);
		click(jq("$btn1"));
		waitResponse();

		assertNoJSError();
		Assertions.assertEquals(2, jq("$gb1 @window").length());
		Assertions.assertEquals(2, jq("$gb1 @textbox").length());
	}

	private void checkNestedDivGroupbox() {
		click(jq("$btn2"));
		waitResponse();
		click(jq("$gb2"));
		waitResponse(true);

		assertNoJSError();
		Assertions.assertEquals(2, jq("$div2 @window").length());
		Assertions.assertEquals(2, jq("$div2 @textbox").length());
	}
}
