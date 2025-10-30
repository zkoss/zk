/* B86_ZK_3982Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 22 16:25:17 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3982Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		jq(".z-listbox-body").scrollTop(1000);
		click(jq(".z-listgroup-icon > i").eq(2));
		waitResponse();
		click(jq(".z-listgroup-icon > i").eq(1));
		waitResponse();
		Assertions.assertEquals(0, jq(".z-listbox-body").scrollTop());
	}
}
