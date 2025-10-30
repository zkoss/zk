/* B85_ZK_3898Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 13 12:42:45 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3898Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();

		rightClick(jq("@listitem"));
		waitResponse();

		rightClick(jq("@button:eq(1)"));
		waitResponse();

		click(jq("@button:eq(2)"));
		waitResponse();

		Assertions.assertEquals("The parent of Popup(Option 2) is \"z-page.\"", getZKLog());
	}
}
