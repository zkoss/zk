/* F50_3057903Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 17:32:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F50_3057903Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testSliderPageIncrement("@slider:eq(0)");
		testSliderPageIncrement("@slider:eq(1)");
		testSliderPageIncrement("@slider:eq(2)");
		testSliderPageIncrement("@slider:eq(3)");
	}

	private void testSliderPageIncrement(String expr) {
		Widget slider = widget(expr);
		click(slider);
		waitResponse(true);
		Assertions.assertEquals("10", slider.$n("btn").get("title"));
		click(slider.$n("area"));
		waitResponse(true);
		Assertions.assertEquals("0", slider.$n("btn").get("title"));
	}
}
