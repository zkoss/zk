/* B50_2984382Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 12 18:49:05 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B50_2984382Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final Widget combo = widget("@combobox");
		click(combo.$n("btn"));
		waitResponse(true);

		Assertions.assertEquals(jq("@combobox").offsetLeft(), jq(combo.$n("pp")).offsetLeft(), 1);
	}
}
