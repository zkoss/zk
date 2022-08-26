/* B96_ZK_4970Test.java

	Purpose:
		
	Description:
		
	History:
		4:37 PM 2021/11/18, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_4970Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(create)"));
		waitResponse();
		assertEquals(3, jq(".z-listitem").length());
	}
}
