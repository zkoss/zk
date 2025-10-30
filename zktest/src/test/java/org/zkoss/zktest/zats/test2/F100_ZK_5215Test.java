/* F100_ZK_5215Test.java

	Purpose:
		
	Description:
		
	History:
		10:23 AM 2023/7/20, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
/**
 * @author jumperchen
 */
public class F100_ZK_5215Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("rgb(255, 0, 0)", jq("body").css("background-color"));
	}
}
