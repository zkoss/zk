/* B85_ZK_3950Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 12 16:39:09 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3950Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals(jq("@tabbox").outerHeight(), jq(".z-tabpanels").outerHeight());
		Assertions.assertTrue(jq(".z-tab").eq(2).isVisible());
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals(jq("@tabbox").outerHeight(), jq(".z-tabpanels").outerHeight());
		Assertions.assertFalse(jq(".z-tab").eq(2).isVisible());
	}
}
