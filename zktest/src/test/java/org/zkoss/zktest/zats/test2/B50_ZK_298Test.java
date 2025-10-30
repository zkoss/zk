/* B50_ZK_298Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 08 10:42:00 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_298Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int lastHeight = jq(".z-tabbox").height();
		Assertions.assertEquals(jq(".z-tabpanels").outerHeight(), jq(".z-tabbox").height());
		
		click(jq(".z-tab").eq(1));
		waitResponse();
		Assertions.assertEquals(jq(".z-tabpanels").outerHeight(), jq(".z-tabbox").height());
		Assertions.assertNotEquals(lastHeight, jq(".z-tabbox").height());
		lastHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(2));
		waitResponse();
		Assertions.assertEquals(jq(".z-tabpanels").outerHeight(), jq(".z-tabbox").height());
		Assertions.assertNotEquals(lastHeight, jq(".z-tabbox").height());
		
		click(jq(".z-button").eq(0));
		waitResponse();
		int fixedHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(1));
		waitResponse();
		Assertions.assertEquals(fixedHeight, jq(".z-tabbox").height());
		
		click(jq(".z-tab").eq(0));
		waitResponse();
		Assertions.assertEquals(fixedHeight, jq(".z-tabbox").height());
		
		click(jq(".z-button").eq(1));
		waitResponse();
		lastHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(1));
		waitResponse();
		Assertions.assertNotEquals(lastHeight, jq(".z-tabbox").height());
		lastHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(2));
		waitResponse();
		Assertions.assertNotEquals(fixedHeight, jq(".z-tabbox").height());
	}
}
