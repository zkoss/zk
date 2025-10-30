/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Thu May 06 18:24:49 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.propertybinding;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class BasicPropertyBindingClientTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/databinding/propertybinding/BasicPropertyBinding.zul");
		//[Step 2]
		JQuery jqMsg = jq("$msg2");
		assertEquals("false", jqMsg.text());
		JQuery maxiBtn = jq(".z-window-maximize");
		click(maxiBtn);
		waitResponse();
		assertEquals("true", jqMsg.text());
		click(maxiBtn);
		waitResponse();
		assertEquals("false", jqMsg.text());
		click(maxiBtn);
		waitResponse();
		assertEquals("true", jqMsg.text());
	}
}
