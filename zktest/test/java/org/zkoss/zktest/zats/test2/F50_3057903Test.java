/* F50_3057903Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 17:32:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

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
		Assert.assertEquals("10", slider.$n("btn").get("title"));
		click(slider.$n("area"));
		waitResponse(true);
		Assert.assertEquals("0", slider.$n("btn").get("title"));
	}
}
