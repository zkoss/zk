/* B50_3357931Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 02 16:55:23 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3357931Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-textbox").eq(0));
		waitResponse();
		click(jq(".z-vlayout-inner>.z-button"));
		waitResponse();
		Assertions.assertNotEquals("A wrong value exception was caught!", jq(".z-window-content .z-label").text());
	}
}
