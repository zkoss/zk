/* B50_3091949Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 12:31:43 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3091949Test extends WebDriverTestCase {

	private JQuery iframe;

	@Test
	public void test() {
		connect();
		iframe = jq(".z-iframe");
		testIframe(jq(".z-bandbox-button"));
		testIframe(jq(".z-combobox-button"));
		testIframe(jq(".z-datebox-button"));
		testIframe(jq(".z-menu"));
	}

	private void testIframe(JQuery button) {
		click(button);
		waitResponse();
		Assertions.assertEquals("hidden", iframe.css("visibility"));
	}
}
