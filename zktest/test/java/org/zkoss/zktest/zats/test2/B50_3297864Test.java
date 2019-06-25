/* B50_3297864Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:16:02 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

@NotThreadSafe
public class B50_3297864Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery buttons = jq(".z-button");
		try {
			JQuery decimalbox = jq(".z-decimalbox");
			click(buttons.eq(0));
			waitResponse();
			sendKeys(decimalbox, ",01");
			waitResponse();
			click(jq("ol"));
			waitResponse();
			Assert.assertEquals("0,01", decimalbox.val());
		} finally {
			click(buttons.eq(1));
			waitResponse();
		}
	}
}
