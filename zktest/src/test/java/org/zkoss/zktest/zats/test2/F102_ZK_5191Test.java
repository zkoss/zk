/* F102_ZK_5191Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 09 15:39:19 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class F102_ZK_5191Test extends WebDriverTestCase {

	@Test
	public void test(){
		connect();
		String[] types = {
				"text", "password", "tel", "email", "url", "button", "radio", "search", "date",
				"checkbox", "color", "datetime-local", "file", "hidden", "image", "month",
				"number", "range", "reset", "submit", "time", "week"
		};
		for (int i = 0; i < jq("@textbox").length(); i++) {
			Widget tb = jq("@textbox").eq(i).toWidget();
			String type = tb.$n().get("type");
			assertEquals(types[i], type);
		}
	}
}


