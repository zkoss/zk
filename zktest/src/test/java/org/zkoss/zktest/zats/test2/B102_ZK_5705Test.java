/* B102_ZK_5705Test.java

	Purpose:
		
	Description:
		
	History:
		10:21 AM 2025/4/22, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B102_ZK_5705Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String origin = jq("@label").text();
		click(jq("@button:contains(refresh)"));
		waitResponse();
		assertNotEquals(origin, jq("@label").text());
	}
}
