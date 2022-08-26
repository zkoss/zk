/* F50_3061671Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 16:36:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3061671Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertEquals("btn", getMessageBoxContent());
		closeMessageBox();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals("btn", getMessageBoxContent());
		closeMessageBox();
		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertEquals("btn", getMessageBoxContent());
	}

	private void closeMessageBox() {
		click(jq(".z-messagebox-button:contains(OK)"));
		waitResponse();
	}
}
