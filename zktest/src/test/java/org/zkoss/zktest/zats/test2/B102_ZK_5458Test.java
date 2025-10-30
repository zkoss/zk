/* B102_ZK_5458Test.java

	Purpose:
		
	Description:
		
	History:
		3:10 PM 2025/4/7, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B102_ZK_5458Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect("/cdi/B102-ZK-5458.zul");
		waitResponse();
		click(jq("@checkbox"));
		waitResponse();
		click(jq("@button:contains(List Order)"));
		waitResponse();
		assertEquals(10, jq("@row").length());
	}
}
