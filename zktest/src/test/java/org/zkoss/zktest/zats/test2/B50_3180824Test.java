/* B50_3180824Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 18:41:58 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3180824Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery select = jq(".z-select");
		JQuery option = jq(".z-option:contains(A)");
		int scrollTop = select.scrollTop();

		click(option);
		waitResponse();

		Assertions.assertTrue(option.is(":selected"));
		Assertions.assertEquals(scrollTop, select.scrollTop());
	}
}
