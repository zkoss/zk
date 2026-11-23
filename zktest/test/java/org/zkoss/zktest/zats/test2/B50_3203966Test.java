/* B50_3203966Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 10:13:51 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3203966Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery buttons = jq(".z-button");
		for (int i = 0; i < 2; i++) {
			click(buttons.eq(i));
			waitResponse();
		}
		Assert.assertEquals("Correct", jq(".z-label").text());
	}
}
