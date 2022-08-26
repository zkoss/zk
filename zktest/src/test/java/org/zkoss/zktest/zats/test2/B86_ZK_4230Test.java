/* B86_ZK_4230Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 07 12:56:00 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4230Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery toolbar = jq(".z-signature-toolbar");
		JQuery toggle = jq(".z-button");
		Assertions.assertFalse(toolbar.isVisible());
		click(toggle);
		waitResponse();
		Assertions.assertTrue(toolbar.isVisible());
		click(toggle);
		waitResponse();
		Assertions.assertFalse(toolbar.isVisible());
	}
}
