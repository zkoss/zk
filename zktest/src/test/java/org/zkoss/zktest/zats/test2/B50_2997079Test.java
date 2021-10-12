/* B50_2997079Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 12:15:33 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.Color;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2997079Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();

		Assert.assertEquals(
				Color.fromString("blue"),
				Color.fromString(jq("@label").css("backgroundColor"))
		);
	}
}
