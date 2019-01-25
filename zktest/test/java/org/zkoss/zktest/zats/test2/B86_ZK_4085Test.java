/* B86_ZK_4085Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 08 11:10:24 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4085Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery sliders = jq(".z-slider");
		testStrokeWidth(sliders.eq(0));
		testStrokeWidth(sliders.eq(1));
	}

	private void testStrokeWidth(JQuery slider) {
		Assert.assertTrue(Integer.valueOf(slider.find("path").attr("stroke-width"))
				<= Math.min(slider.outerWidth(), slider.outerHeight()) / 2);
	}
}
