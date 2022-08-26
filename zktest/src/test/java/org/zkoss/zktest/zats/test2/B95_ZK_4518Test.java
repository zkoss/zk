/* B95_ZK_4518Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 28 16:34:28 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B95_ZK_4518Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery box = jq("@decimalbox");
		click(box);
		waitResponse();

		sendKeys(box, "-3%");
		click(jq("@button"));
		waitResponse();

		Assertions.assertFalse(hasError(), "Shouldn't have an invalid message");
	}
}
