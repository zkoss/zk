/* B50_ZK_647Test.java

	Purpose:

	Description:

	History:
		11:27 AM 2024/10/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B50_ZK_647Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("color:#0000CC; font-family:\"courier new\",\"times new roman\",\"黑体\"; font-size:30px;", jq("$label").attr("style"));
	}
}
